package nextstep.subway.line.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.NotFoundSectionException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Sections {

    private static final String ERR_MSG_CAN_NOT_ADD_SECTION = "상행역과 하행역 둥 중 하나도 포함되어있지 않아서 추가할 수 없습니다.";
    private static final String ERR_MSG_EXIST_SECTION = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.";
    private static final String ERR_MSG_GREATER_THAN_DISTANCE_OF_EXSISTING_SECTION = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";

    private static final String ERR_MSG_NOT_FOUND_SECTION = "구간이 존재하지 않습니다.";
    private static final String ERR_MSG_CAN_NOT_REMOVE_SECTION = "구간을 삭제 할 수 없습니다.";


    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        if(sections.isEmpty()) {
            sections.add(section);
            return;
        }
        addSectionToLine(section);
    }

    private void addSectionToLine(Section section) {
        if(existsStationInSection(section.getUpStation())) {
            addSectionByUpStation(section);
            return;
        }

        if(existsStationInSection(section.getDownStation())) {
            addSectionByDownStation(section);
            return;
        }

        throw new IllegalStateException(ERR_MSG_CAN_NOT_ADD_SECTION);
    }


    private void addSectionByUpStation(Section section) {

        if(existsStationInSection(section.getDownStation())) {
            throw new IllegalStateException(ERR_MSG_EXIST_SECTION);
        }

        Section existSection = findSectionByUpStation(section);

        if(existSection == null) {
            sections.add(section);
            return;
        }

        if(section.isGreaterThan(existSection.getDistance())) {
            throw new IllegalStateException(ERR_MSG_GREATER_THAN_DISTANCE_OF_EXSISTING_SECTION);
        }

        Section newSection = createNewSection(section.getDownStation(), existSection.getDownStation(), existSection.getDistance().getDistanceBetweenTwoDistances(section.getDistance()));
        sections.remove(existSection);
        sections.add(newSection);
        sections.add(section);
    }

    private Section findSectionByUpStation(Section targetSection) {
        Map<Station, Section> upToDownSections = new HashMap<>();
        sections.forEach(
                section -> upToDownSections.put(section.getUpStation(), section)
        );

        return upToDownSections.get(targetSection.getUpStation());
    }

    private void addSectionByDownStation(Section section) {
        if(existsStationInSection(section.getUpStation())) {
            throw new IllegalStateException(ERR_MSG_EXIST_SECTION);
        }

        Section existSection = findSectionByDownStaionId(section);

        if(existSection == null) {
            sections.add(section);
            return;
        }

        if(section.getDistance().isGreaterThan(existSection.getDistance())) {
            throw new IllegalStateException(ERR_MSG_GREATER_THAN_DISTANCE_OF_EXSISTING_SECTION);
        }

        Section newSection = createNewSection(section.getDownStation(), existSection.getDownStation(), existSection.getDistance().getDistanceBetweenTwoDistances(section.getDistance()));
        sections.remove(existSection);
        sections.add(newSection);
        sections.add(section);
    }

    private Section findSectionByDownStaionId(Section targetSection) {
        Map<Station, Section> downToUpSections = new HashMap<>();
        sections.forEach(
                section -> downToUpSections.put(section.getDownStation(), section)
        );

        return downToUpSections.get(targetSection.getDownStation());
    }

    private boolean existsStationInSection(Station station) {
        return extractAllStations().contains(station);
    }

    public Set<Station> extractAllStations() {
        Set<Station> stations = new HashSet<>();

        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return stations;
    }

    private Section createNewSection(Station upSatation, Station downStation, int distance) {
        return Section.of(upSatation, downStation, distance);
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        Section section = getHeadSection();
        return getOrderedStations(section);
    }

    private List<Station> getOrderedStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());
        while ((section = getNextSection(section)) != null) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    protected Section getHeadSection() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();
        for (Section section : sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }
        upStations.removeAll(downStations);
        Station upStationOfHeadSection = upStations.get(0);
        return findByUpStation(upStationOfHeadSection)
                .orElseThrow(() -> {
                    throw new NotFoundStationException(String.format("%s", upStationOfHeadSection.getId()));
                });
    }

    private Optional<Section> findByUpStation(Station station) {
        return sections.stream()
                .filter(s -> station.isSameName(s.getUpStation()))
                .findFirst();
    }

    public Section getNextSection(Section section) {
        Station downStation = section.getDownStation();
        return sections.stream()
                .filter(s -> downStation.isSameName(s.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    public void remove(Station station) {
        validationRemove(station);
        Section beforeSection = findDownStationSection(station);
        Section afterSection = findUpStationSection(station);
        Station lastStation = findLastSection().getDownStation();
        Station firstStation = findFirstSection().getUpStation();

        if (station.equals(lastStation)) {
            sections.remove(beforeSection);
            return;
        }

        if (station.equals(firstStation)) {
            sections.remove(afterSection);
            return;
        }

        beforeSection.updateDownStationToDown(afterSection);
        sections.remove(afterSection);
    }

    private void validationRemove(Station station) {
        Set<Station> stations = extractAllStations();

        if (CollectionUtils.isEmpty(sections) || !stations.contains(station)) {
            throw new IllegalStateException(ERR_MSG_NOT_FOUND_SECTION);
        }

        if (sections.size() == 1) {
            throw new IllegalStateException(ERR_MSG_CAN_NOT_REMOVE_SECTION);
        }
    }

    private Section findDownStationSection(Station station) {
        return sections.stream().filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElse(Section.EMPTY);
    }

    private Section findUpStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElse(Section.EMPTY);
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(section -> findSectionIsAnotherDownStation(section) == null)
                .findFirst()
                .orElseThrow(() -> new NotFoundSectionException(ERR_MSG_NOT_FOUND_SECTION));
    }

    private Section findLastSection() {
        return sections.stream()
                .filter(section -> findSectionIsAnotherUpStation(section) == null)
                .findFirst()
                .orElseThrow(() -> new NotFoundSectionException(ERR_MSG_NOT_FOUND_SECTION));
    }

    private Section findSectionIsAnotherUpStation(Section beforeSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(beforeSection.getDownStation()))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionIsAnotherDownStation(Section beforeSection) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(beforeSection.getUpStation()))
                .findFirst()
                .orElse(null);
    }
}

