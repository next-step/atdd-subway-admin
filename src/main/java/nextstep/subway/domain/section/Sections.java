package nextstep.subway.domain.section;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.NotFoundEntityException;
import nextstep.subway.message.SectionMessage;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true )
    private final List<Section> sectionItems;

    public Sections() {
        this.sectionItems = new ArrayList<>();
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

        Section upTerminalStationSection = getUpTerminalStationSection();
        if(upTerminalStationSection.isSameUpStation(section.getDownStation())) {
            this.sectionItems.add(0, section);
        }
    }

    private Section getUpTerminalStationSection() {
        if(this.sectionItems.isEmpty()) {
            throw new IllegalArgumentException(SectionMessage.ERROR_EMPTY_SECTIONS.message());
        }
        return this.sectionItems.get(0);
    }

    private void addDownTerminalStationSection(Section section) {
        Section downTerminalStationSection = getDownTerminalStationSection();
        if(downTerminalStationSection.isSameDownStation(section.getUpStation())) {
            this.sectionItems.add(section);
        }
    }

    private Section getDownTerminalStationSection() {
        if(this.sectionItems.isEmpty()) {
            throw new IllegalArgumentException(SectionMessage.ERROR_EMPTY_SECTIONS.message());
        }
        return this.sectionItems.get(this.sectionItems.size() - 1);
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

    public void removeByStation(Station station) {
        validateRemoveSection(station);
        removeUpTerminalStationSectionBy(station);
        removeDownTerminalStationSectionBy(station);
        removeMiddleStationSectionBy(station);
    }

    private void validateRemoveSection(Station station) {
        if(this.sectionItems.size() < 2) {
            throw new IllegalArgumentException(SectionMessage.ERROR_SECTIONS_MORE_THAN_TWO_SECTIONS.message());
        }

        if(!hasStation(station)) {
            throw new IllegalArgumentException(SectionMessage.ERROR_NOT_FOUND_STATION.message());
        }
    }

    private void removeUpTerminalStationSectionBy(Station station) {
        Section upTerminalSection = getUpTerminalStationSection();
        if(upTerminalSection.isSameUpStation(station)) {
            this.sectionItems.remove(upTerminalSection);
        }
    }

    private void removeDownTerminalStationSectionBy(Station station) {
        if(!hasStation(station)) {
            return;
        }

        Section downTerminalSection = getDownTerminalStationSection();
        if(downTerminalSection.isSameDownStation(station)) {
            this.sectionItems.remove(downTerminalSection);
        }
    }

    private void removeMiddleStationSectionBy(Station station) {
        if(!hasStation(station)) {
            return;
        }

        Section sectionWithSameUpStation = getSectionWithSameUpStation(station);
        Section sectionWithSameDownStation = getSectionWithSameDownStation(station);

        sectionWithSameDownStation.changeDownStation(sectionWithSameUpStation.getDownStation());
        sectionWithSameDownStation.plusDistance(sectionWithSameUpStation);
        this.sectionItems.remove(sectionWithSameUpStation);
    }

    private boolean hasStation(Station station) {
        return this.sectionItems.stream()
                .anyMatch(sectionItem -> sectionItem.hasStation(station));
    }
}
