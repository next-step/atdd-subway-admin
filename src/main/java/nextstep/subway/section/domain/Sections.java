package nextstep.subway.section.domain;

import nextstep.subway.common.ErrorCode;
import nextstep.subway.exception.NotAcceptableApiException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

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
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();
        for (Section section : sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }
        upStations.removeAll(downStations);
        Station firstUpStation = upStations.get(0);
        return getSectionByUpStation(firstUpStation);
    }

    private Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> Objects.equals(section.getUpStation().getId(), upStation.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("%s (stationId: %s)", NOT_EXIST_STATION, upStation.getId())));
    }

    private boolean isInsertBetweenSection(Section section) {
        return isUpdateUpStation(section) || isUpdateDownStation(section);
    }

    private boolean isUpdateUpStation(Section newSection) {
        Station newUpStation = newSection.getUpStation();

        if (hasMatchWithUpStation(newUpStation)) {
            Section oldSection = getOldSectionByUpStation(newUpStation);
            validateDistance(newSection, oldSection);

            oldSection.setUpStation(newSection.getDownStation());
            oldSection.setDistance(oldSection.getDistance() - newSection.getDistance());

            sections.add(newSection);
            return true;
        }
        return false;
    }

    private boolean isUpdateDownStation(Section newSection) {
        Station newDownStation = newSection.getDownStation();

        if (hasMatchWithDownStation(newDownStation)) {
            Section oldSection = getOldSectionByDownStation(newDownStation);
            validateDistance(newSection, oldSection);

            oldSection.setDownStation(newSection.getUpStation());
            oldSection.setDistance(oldSection.getDistance() - newSection.getDistance());

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

    private void validateDistance(Section newSection, Section oldSection) {
        if (oldSection.getDistance() <= newSection.getDistance()) {
            throw new NotAcceptableApiException(ErrorCode.INVALID_SECTION_DISTANCE);
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

    private List<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return new ArrayList<>(stations);
    }

    private boolean hasMatchWithUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isEqualUpStation(station));
    }

    private Section getOldSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("상행역이 일치하는 기존 구간이 없습니다. (upStationId: %d)", station.getId())));
    }

    private boolean hasMatchWithDownStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isEqualDownStation(station));
    }

    private Section getOldSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualDownStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("하행역이 일치하는 기존 구간이 없습니다. (downStationId: %d)", station.getId())));
    }
}
