package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final String DUPLICATED_STATIONS = "이미 등록된 역 구간을 다시 등록 할 수 없습니다.";
    private static final String NOT_CONTAINS_NEITHER_STATIONS = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";
    private static final String EMPTY_SECTIONS = "등록된 구간이 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        if (!sections.contains(section)) {
            this.sections.add(section);
        }
    }

    public void registerNewSection(Section newSection) {
        if (this.sections.size() == 0) {
            add(newSection);
            return;
        }
        validateNewSection(newSection);
        this.sections = sections.stream()
                .flatMap(section -> section.insertNewSection(newSection).stream())
                .collect(Collectors.toList());
    }

    private void validateNewSection(Section newSection) {
        if (hasBothStation(newSection)) {
            throw new IllegalArgumentException(DUPLICATED_STATIONS);
        }
        if (hasNotBothStation(newSection)) {
            throw new IllegalArgumentException(NOT_CONTAINS_NEITHER_STATIONS);
        }
    }

    private boolean hasBothStation(Section section) {
        Set<Station> stations = getStations();
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    private boolean hasNotBothStation(Section section) {
        Set<Station> stations = getStations();
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }

    private List<Section> getSortedSections() {
        List<Section> sortedSections = new ArrayList<>();
        Section currentSection = findFirstSection();
        sortedSections.add(currentSection);
        addNextSectionIfExist(findNextSection(currentSection), sortedSections);
        return sortedSections;
    }

    private void addNextSectionIfExist(Optional<Section> maybeNextSection, List<Section> sortedSections) {
        if (!maybeNextSection.isPresent()) {
            return;
        }
        Section section = maybeNextSection.get();
        sortedSections.add(section);
        addNextSectionIfExist(findNextSection(section), sortedSections);
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(this::isHead)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(EMPTY_SECTIONS));
    }

    private boolean isHead(Section compare) {
        return sections.stream()
                .filter(origin -> !compare.isSameEdges(origin))
                .noneMatch(compare::isAfter);
    }

    private Optional<Section> findNextSection(Section compare) {
        return sections.stream()
                .filter(origin -> !compare.isSameEdges(origin))
                .filter(origin -> origin.isAfter(compare))
                .findFirst();
    }

    public Set<Station> getStations() {
        return this.getSortedSections().stream()
                .flatMap(section -> section.getUpAndDownStations()
                        .stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
