package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void addSection(Section section) {
        validate(section);
        for (Section target : sections) {
            target.rebase(section);
        }
        sections.add(section);
    }

    public void removeSectionContainsBy(Station station) {
        Optional<Section> upSection = getUpSection(station);
        Optional<Section> downSection = getDownSection(station);
        mergeSection(upSection, downSection);
    }

    public List<Station> getOrderedStations() {
        Optional<Section> firstSection = getFirstSection();

        Set<Station> result = new LinkedHashSet<>();
        while (firstSection.isPresent()) {
            Section currentSection = firstSection.get();
            result.addAll(currentSection.getStations());
            firstSection = sections.stream()
                    .filter(section -> Objects.equals(section.getUpStation(), currentSection.getDownStation()))
                    .findFirst();
        }
        return new ArrayList<>(result);
    }

    private Optional<Section> getUpSection(Station station) {
        return sections.stream()
                .filter(upSection -> Objects.equals(upSection.getDownStation(), station))
                .findFirst();
    }

    private Optional<Section> getDownSection(Station station) {
        return sections.stream()
                .filter(downSection -> Objects.equals(downSection.getUpStation(), station))
                .findFirst();
    }

    private Optional<Section> getFirstSection() {
        Section currentSection = null;
        Optional<Section> anySection = sections.stream()
                .findFirst();

        while (anySection.isPresent()) {
            currentSection = anySection.get();
            anySection = getPreviousSection(currentSection);
        }
        return Optional.ofNullable(currentSection);
    }

    private Optional<Section> getPreviousSection(Section currentSection) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getDownStation(), currentSection.getUpStation()))
                .findFirst();
    }

    private void validate(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        Set<Station> matchedStations = getMatchedSectionStations(section);
        if (matchedStations.containsAll(section.getStations())) {
            throw new IllegalArgumentException("추가 하고자 하는 상행역과 하행역이 이미 노선에 등록되어 있습니다");
        }
        if (matchedStations.isEmpty()) {
            throw new IllegalArgumentException("추가 하고자 하는 상행역과 하행역 중 매칭되는 역이 없습니다");
        }

    }

    private Set<Station> getMatchedSectionStations(Section section) {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .filter(section::containsStation)
                .collect(Collectors.toSet());
    }

    private void mergeSection(Optional<Section> upSection, Optional<Section> downSection) {
        validate(upSection, downSection);
        mergeMiddleSection(upSection, downSection);
        mergeLastSection(upSection, downSection);
    }

    private void validate(Optional<Section> upSection, Optional<Section> downSection) {
        if (isAnyPresent(upSection, downSection)) {
            throwExceptionIfSectionsHasOneSection();
        }
    }

    private void mergeMiddleSection(Optional<Section> upSection, Optional<Section> downSection) {
        if (isMiddleSection(upSection, downSection)) {
            Section up = upSection.get();
            Section down = downSection.get();
            down.mergeUpStation(up);
            sections.remove(up);
        }
    }

    private void mergeLastSection(Optional<Section> upSection, Optional<Section> downSection) {
        if (!isMiddleSection(upSection, downSection)) {
            upSection.ifPresent(up -> sections.remove(up));
            downSection.ifPresent(down -> sections.remove(down));
        }
    }

    private boolean isMiddleSection(Optional<Section> upSection, Optional<Section> downSection) {
        return upSection.isPresent() && downSection.isPresent();
    }

    private boolean isAnyPresent(Optional<Section> upSection, Optional<Section> downSection) {
        return upSection.isPresent() || downSection.isPresent();
    }

    private void throwExceptionIfSectionsHasOneSection() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("하나 남은 구간의 종점은 삭제할 수 없습니다.");
        }
    }
}
