package nextstep.subway.domain;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final String DUPLICATE_UP_DOWN_STATIONS = "상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.";
    private static final String NOT_INCLUDE_UP_DOWN_STATIONS = "상행역과 하행역 모두 노선에 포함되어 있지 않습니다.";
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new LinkedList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public Set<Station> assignedStations() {
        Set<Station> sortedStations = new LinkedHashSet<>();
        Optional<Section> section = findFirstSection();
        while (section.isPresent()) {
            section.ifPresent(findSection -> sortedStations.addAll(findSection.stations()));
            section = findNextSection(section.get());
        }
        return sortedStations;
    }

    private Optional<Section> findFirstSection() {
        List<Station> downStations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());
        return this.sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findNextSection(Section currentSection) {
        return this.sections.stream()
                .filter(section -> section.isNext(currentSection))
                .findFirst();
    }

    public void add(Section newSection) {
        validateDuplicateUpDownStation(newSection);
        validateNotIncludeUpDownStation(newSection);
        sections.forEach(section -> section.reorganize(newSection));
        sections.add(newSection);
    }

    private void validateDuplicateUpDownStation(Section newSection) {
        boolean isSame = this.sections.stream().anyMatch(section -> section.isSameUpDownStation(newSection));
        if (isSame) {
            throw new IllegalArgumentException(DUPLICATE_UP_DOWN_STATIONS);
        }
    }

    private void validateNotIncludeUpDownStation(Section newSection) {
        if (notInclude(newSection)) {
            throw new IllegalArgumentException(NOT_INCLUDE_UP_DOWN_STATIONS);
        }
    }

    private boolean notInclude(Section newSection) {
        Set<Station> assignedStations = this.assignedStations();
        return newSection.stations().stream()
                .noneMatch(assignedStations::contains);
    }

    public void delete(Station station) {
        Optional<Section> prevSection = findPrevSection(station);
        Optional<Section> nextSection = findNextSection(station);
        if (isDeleteMiddle(prevSection, nextSection)) {
            deleteMiddle(prevSection.get(), nextSection.get());
            return;
        }
        nextSection.ifPresent(sections::remove);
        prevSection.ifPresent(sections::remove);
    }

    private Optional<Section> findPrevSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isDownStation(station))
                .findFirst();
    }

    private Optional<Section> findNextSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.isUpStation(station))
                .findFirst();
    }

    private boolean isDeleteMiddle(Optional<Section> prevSection, Optional<Section> nextSection) {
        return prevSection.isPresent() && nextSection.isPresent();
    }

    private void deleteMiddle(Section prevSection, Section nextSection) {
        prevSection.merge(nextSection);
        this.sections.remove(nextSection);
    }
}
