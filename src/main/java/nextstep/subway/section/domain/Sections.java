package nextstep.subway.section.domain;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.exception.NotAcceptableApiException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Embeddable
public class Sections {

    protected static final String SECTION_CAN_NOT_BE_NULL = "section null일 수 없습니다.";
    protected static final String DUPLICATE_SECTION = "이미 등록된 section입니다.";
    protected static final String NOT_EXIST_STATION = "존재하지 않는 역입니다.";

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        validate(section);

        if (isInsertBetweenSection(section)) {
            return;
        }
        sections.add(section);
    }

    public Section getFirstSection() {
        Stations upStations = getUpStations();
        Stations downStations = getDownStations();

        upStations.removeAll(downStations);
        Station firstUpStation = upStations.getFirstStation();
        return getSectionByUpStation(firstUpStation);
    }

    public Section getLastSection() {
        Stations upStations = getUpStations();
        Stations downStations = getDownStations();

        downStations.removeAll(upStations);
        Station lastDownStation = downStations.getLastStation();
        return getSectionByDownStation(lastDownStation);
    }

    private Stations getUpStations() {
        Stations stations = new Stations();
        sections.forEach(section -> stations.add(section.getUpStation()));
        return stations;
    }

    private Stations getDownStations() {
        Stations stations = new Stations();
        sections.forEach(section -> stations.add(section.getDownStation()));
        return stations;
    }

    private Section getSectionByUpStation(Station station) {
        return sections.stream()
                .filter(station::isUpStation)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("%s (stationId: %s)", NOT_EXIST_STATION, station.getId())));
    }

    private Section getSectionByDownStation(Station station) {
        return sections.stream()
                .filter(station::isDownStation)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("%s (stationId: %s)", NOT_EXIST_STATION, station.getId())));
    }

    private boolean isInsertBetweenSection(Section section) {
        return isUpdateUpStation(section) || isUpdateDownStation(section);
    }

    private boolean isUpdateUpStation(Section newSection) {
        Station newUpStation = newSection.getUpStation();

        if (hasMatchWithUpStation(newUpStation)) {
            Section oldSection = getOldSectionByUpStation(newUpStation);
            oldSection.updateUpStation(newSection);

            sections.add(newSection);
            return true;
        }
        return false;
    }

    private boolean isUpdateDownStation(Section newSection) {
        Station newDownStation = newSection.getDownStation();

        if (hasMatchWithDownStation(newDownStation)) {
            Section oldSection = getOldSectionByDownStation(newDownStation);
            oldSection.updateDownStation(newSection);

            sections.add(newSection);
            return true;
        }
        return false;
    }

    private void validate(Section section) {
        if (section == null) {
            throw new IllegalArgumentException(SECTION_CAN_NOT_BE_NULL);
        }
        if (sections.contains(section)) {
            throw new IllegalArgumentException(DUPLICATE_SECTION);
        }
        if (isExistStations(section.getUpStation(), section.getDownStation())) {
            throw new NotAcceptableApiException(ErrorCode.EXIST_STATIONS);
        }
        if (isNotExistStations(section.getUpStation(), section.getDownStation())) {
            throw new NotAcceptableApiException(ErrorCode.MUST_CONTAIN_STATION);
        }
    }

    private boolean isExistStations(Station... stations) {
        return Arrays.stream(stations)
                .allMatch(station -> getStations().contains(station));
    }

    private boolean isNotExistStations(Station... stations) {
        if (getStations().isEmpty()) {
            return false;
        }
        return Arrays.stream(stations)
                .noneMatch(station -> getStations().contains(station));
    }

    private Stations getStations() {
        Stations stations = new Stations();
        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return stations;
    }

    private boolean hasMatchWithUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isEqualUpStation(station));
    }

    public Section getOldSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("상행역이 일치하는 기존 구간이 없습니다. (upStationId: %d)", station.getId())));
    }

    public boolean removeSection(Section section) {
        return sections.remove(section);
    }

    private boolean hasMatchWithDownStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isEqualDownStation(station));
    }

    public Section getOldSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualDownStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("하행역이 일치하는 기존 구간이 없습니다. (downStationId: %d)", station.getId())));
    }
}
