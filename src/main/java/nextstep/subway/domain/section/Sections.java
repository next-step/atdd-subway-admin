package nextstep.subway.domain.section;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.NotFoundEntityException;
import nextstep.subway.message.SectionMessage;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true )
    private final List<Section> sectionItems;

    public Sections() {
        this.sectionItems = new ArrayList<>();
    }

    public Sections(List<Section> sectionItems) {
        this.sectionItems = getOrderedStations(sectionItems);
    }

    private List<Section> getOrderedStations(List<Section> sections) {
        if(sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Section> orderedSections = new ArrayList<>();
        Map<Station, Station> sectionUpStations = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        Station upStation = getUpTerminalStation(sectionUpStations);
        while(sectionUpStations.containsKey(upStation)) {
            orderedSections.add(getSectionWithSameUpStation(upStation));
            upStation = sectionUpStations.get(upStation);
        }
        return orderedSections;
    }

    private Station getUpTerminalStation(Map<Station, Station> sectionStations) {
        Set<Station> downStations = new HashSet<>(sectionStations.values());
        return sectionStations.keySet().stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException(SectionMessage.ERROR_NOT_FOUND_UP_TERMINAL_STATION.message()));
    }

    public Section getSectionWithSameUpStation(Station upStation) {
        return this.sectionItems.stream()
                .filter(sectionItem -> sectionItem.isSameUpStation(upStation))
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException(SectionMessage.ERROR_NOT_FOUND_UP_STATION.message()));
    }

    public Section getSectionWithSameDownStation(Station downStation) {
        return this.sectionItems.stream()
                .filter(sectionItem -> sectionItem.isSameDownStation(downStation))
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException(SectionMessage.ERROR_NOT_FOUND_DOWN_STATION.message()));
    }

    public void add(Section section) {
        validateNewSection(section);
        addUpTerminalStationSection(section);
        addDownTerminalStationSection(section);
        addMiddleStationSection(section);
    }

    private void validateNewSection(Section section) {
        List<Station> stations = getAllStations();

        if(isUpAndDownStationsAlreadyEnrolled(section, stations)) {
            throw new IllegalArgumentException(SectionMessage.ERROR_UP_AND_DOWN_STATIONS_ARE_ALREADY_ENROLLED.message());
        }

        if(isUpAndDownStationsNotEnrolled(section, stations)) {
            throw new IllegalArgumentException(SectionMessage.ERROR_UP_AND_DOWN_STATIONS_ARE_NOT_ENROLLED.message());
        }
    }

    private boolean isUpAndDownStationsAlreadyEnrolled(Section section, List<Station> stations) {
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    private boolean isUpAndDownStationsNotEnrolled(Section section, List<Station> stations) {
        return !this.sectionItems.isEmpty()
                && !stations.contains(section.getUpStation())
                && !stations.contains(section.getDownStation());
    }

    private void addUpTerminalStationSection(Section section) {
        if(this.sectionItems.isEmpty()) {
            this.sectionItems.add(section);
            return;
        }

        Section upTerminalStationSection = this.sectionItems.get(0);
        if(upTerminalStationSection.isSameUpStation(section.getDownStation())) {
            this.sectionItems.add(0, section);
        }
    }

    private void addDownTerminalStationSection(Section section) {
        int sectionItemSize = this.sectionItems.size();
        Section downTerminalStationSection = this.sectionItems.get(sectionItemSize - 1);
        if(downTerminalStationSection.isSameDownStation(section.getUpStation())) {
            this.sectionItems.add(sectionItemSize, section);
        }
    }

    // 역 사이에 새로운 역이 등록된 경우 기존 구간의 거리, 상행역 및 하행역을 변경한다
    private void addMiddleStationSection(Section section) {
        if(this.sectionItems.contains(section)) {
            return;
        }

        // 상행역에 신규 역이 추가되었을 경우, 새로운 구간 하행역으로 등록 된 구간을 변경한다
        if(isAddedUpStation(section)) {
            addMiddleSectionWithNewUpStation(section);
            return;
        }

        // 하행역에 신규 역이 추가되었을 경우, 새로운 구간 상행역으로 등록 된 구간을 변경한다
        addMiddleSectionWithNewDownStation(section);
    }

    private boolean isAddedUpStation(Section section) {
        return this.sectionItems.stream()
                .anyMatch(sectionItem -> sectionItem.isSameDownStation(section.getDownStation()));
    }

    private void addMiddleSectionWithNewUpStation(Section section) {
        Section originSection = getSectionWithSameDownStation(section.getDownStation());
        validateNewSectionDistance(originSection, section);

        originSection.changeDownStation(section.getUpStation());
        originSection.minusDistance(section);

        int originSectionIndex = this.sectionItems.indexOf(originSection);
        this.sectionItems.add(originSectionIndex + 1, section);
    }

    private void addMiddleSectionWithNewDownStation(Section section) {
        Section originSection = getSectionWithSameUpStation(section.getUpStation());
        validateNewSectionDistance(originSection, section);

        originSection.changeUpStation(section.getDownStation());
        originSection.minusDistance(section);

        int originSectionIndex = this.sectionItems.indexOf(originSection);
        this.sectionItems.add(originSectionIndex, section);
    }

    private void validateNewSectionDistance(Section originSection, Section newSection) {
        if(newSection.getDistance().isMoreThan(originSection.getDistance())) {
            throw new IllegalArgumentException(SectionMessage.ERROR_NEW_SECTION_DISTANCE_MORE_THAN_ORIGIN_SECTION.message());
        }
    }

    public void clear() {
        this.sectionItems.clear();
    }

    public List<Station> getAllStations() {
        return this.sectionItems.stream()
                .flatMap(Section::stationsStream)
                .distinct()
                .collect(Collectors.toList());
    }
}
