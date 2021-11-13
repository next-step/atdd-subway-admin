package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

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

    public List<Station> getSortedStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(findFirstSection().getUpStation());

        for (int i = 0; i < sections.size(); i++) {
            Optional<Section> sectionOpt = findSectionByUpStation(stations.get(i));
            stations.add(sectionOpt.map(Section::getDownStation)
                                   .orElseThrow(() -> new IllegalStateException(NOT_EXIST_UP_STATION)));
        }

        return stations;
    }

    public boolean contains(Section section) {
        return this.sections.contains(section);
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
    
    private Optional<Section> findSectionByUpStation(Station upStation) {
        return sections.stream()
                       .filter(section -> section.isSameUpStation(upStation))
                       .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return sections.stream()
                       .filter(section -> section.isSameDownStation(downStation))
                       .findFirst();
    }

    private Section findFirstSection() {
        List<Station> downStations = findDownStations();
        return sections.stream()
                       .filter(section -> !downStations.contains(section.getUpStation()))
                       .findFirst()
                       .orElseThrow(() -> new IllegalStateException(NOT_EXIST_FIRST_SECTION));
    }

    private Section findLastSection() {
        List<Station> upStations = findUpStations();
        return sections.stream()
                       .filter(section -> !upStations.contains(section.getDownStation()))
                       .findFirst()
                       .orElseThrow(() -> new IllegalStateException(NOT_EXIST_LAST_SECTION));
    }

    private List<Station> findUpStations() {
        return sections.stream()
                       .map(Section::getUpStation)
                       .collect(Collectors.toList());
    }
    
    private List<Station> findDownStations() {
        return sections.stream()
                       .map(Section::getDownStation)
                       .collect(Collectors.toList());
    }

    private Section findMiddleSection(Section section) {
        Optional<Section> sectionByUpStation = findSectionByUpStation(section.getUpStation());
        if (sectionByUpStation.isPresent()) {
            return sectionByUpStation.get();
        }

        Optional<Section> sectionByDownStation = findSectionByDownStation(section.getDownStation());
        return sectionByDownStation.orElseThrow(() -> new IllegalStateException(NOT_EXIST_SECTION_BY_STATION));
    }

    private void validateAddableSectionDistance(Section section, Section middleSection) {
        if (section.isGreaterThanOrEqualDistanceTo(middleSection)) {
            throw new IllegalArgumentException(IS_GREATER_OR_EQUAL_DISTANCE);
        }
    }
}
