package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;
import nextstep.subway.utils.StreamUtils;

@Embeddable
public class Sections {
    private static final String NOT_EXIST_FIRST_SECTION = "첫 번째 구간이 존재하지 않습니다.";
    private static final String NOT_EXIST_LAST_SECTION = "마지막 구간이 존재하지 않습니다.";
    private static final String NOT_EXIST_UP_STATION = "구간에 상행 역이 존재하지 않습니다.";
    private static final String NOT_EXIST_SECTION_BY_STATION = "역이 포함된 구간이 없습니다.";
    private static final String IS_GREATER_OR_EQUAL_DISTANCE = "새로운 구간의 길이가 기존 구간길이보다 크거나 같습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections createEmpty() {
        return new Sections(new ArrayList<>());
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        if (!sections.isEmpty() && !isEndSection(section)) {
            Section middleSection = findMiddleSection(section);
            validateAddableSectionDistance(section, middleSection);

            updateMiddleSection(middleSection, section);
        }

        sections.add(section);
    }

    private List<Station> findAllStations() {
        return StreamUtils.flatMapToList(sections, Section::getStations, Collection::stream);
    }

    public boolean retainStations(List<Station> stations) {
        return findAllStations().retainAll(stations);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public boolean contains(Section section) {
        return this.sections.contains(section);
    }

    public boolean containStations(List<Station> stations) {
        return findAllStations().containsAll(stations);
    }

    public List<Station> getSortedStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(findFirstSection().getUpStation());

        for (int i = 0; i < sections.size(); i++) {
            Station station = findSectionByUpStation(stations.get(i))
                                .map(Section::getDownStation)
                                .orElseThrow(() -> new IllegalStateException(NOT_EXIST_UP_STATION));
            stations.add(station);
        }

        return stations;
    }

    private void updateMiddleSection(Section middleSection, Section section) {
        if (middleSection.isSameUpStation(section.getUpStation())) {
            middleSection.changeUpStation(section.getDownStation());
            return;
        }

        middleSection.changeDownStation(section.getUpStation());
    }

    private boolean isEndSection(Section section) {
        Section firstSection = findFirstSection();
        Section lastSection = findLastSection();

        return firstSection.isSameUpStation(section.getDownStation())
            || lastSection.isSameDownStation(section.getUpStation());
    }
    
    private Optional<Section> findSectionByUpStation(Station station) {
        return StreamUtils.filterAndFindFirst(sections, section -> section.isSameUpStation(station));
    }

    private Optional<Section> findSectionByDownStation(Station station) {
        return StreamUtils.filterAndFindFirst(sections, section -> section.isSameDownStation(station));
    }

    private Section findFirstSection() {
        List<Station> downStations = findDownStations();
        return StreamUtils.filterAndFindFirst(sections, section -> !downStations.contains(section.getUpStation()))
                          .orElseThrow(() -> new IllegalStateException(NOT_EXIST_FIRST_SECTION));
    }

    private Section findLastSection() {
        List<Station> upStations = findUpStations();
        return StreamUtils.filterAndFindFirst(sections, section -> !upStations.contains(section.getDownStation()))
                          .orElseThrow(() -> new IllegalStateException(NOT_EXIST_LAST_SECTION));
    }

    private List<Station> findUpStations() {
        return StreamUtils.mapToList(sections, Section::getUpStation);
    }
    
    private List<Station> findDownStations() {
        return StreamUtils.mapToList(sections, Section::getDownStation);
    }

    private Section findMiddleSection(Section section) {
        return findSectionByUpStation(section.getUpStation())
            .orElseGet(() -> findSectionByDownStation(section.getDownStation())
            .orElseThrow(() -> new IllegalStateException(NOT_EXIST_SECTION_BY_STATION)));
    }

    private void validateAddableSectionDistance(Section section, Section middleSection) {
        if (section.isGreaterThanOrEqualDistanceTo(middleSection)) {
            throw new IllegalArgumentException(IS_GREATER_OR_EQUAL_DISTANCE);
        }
    }
}
