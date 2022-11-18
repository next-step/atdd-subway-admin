package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            validateStations(section);
            ifConnectedUpStation(section);
            ifConnectedDownStation(section);
        }
        sections.add(section);
    }

    private void validateStations(Section section) {
        boolean isExistsUpStation = containUpStation(section);
        boolean isExistsDownStation = containDownStation(section);
        if (isExistsUpStation && isExistsDownStation) {
            throw new IllegalArgumentException(UP_STATION_AND_DOWN_STATION_ENROLLMENT.getMessage());
        }
        if (!isExistsUpStation && !isExistsDownStation) {
            throw new IllegalArgumentException(UP_STATION_AND_DOWN_STATION_NOT_FOUND.getMessage());
        }
    }

    private boolean containUpStation(Section section) {
        return distinctStations().contains(section.getUpStation());
    }

    private boolean containDownStation(Section section) {
        return distinctStations().contains(section.getDownStation());
    }

    private List<Station> distinctStations() {
        return sections.stream()
                .flatMap(Section::streamOfStation)
                .distinct()
                .collect(Collectors.toList());
    }

    private void ifConnectedUpStation(Section addSection) {
        sections.forEach(section -> checkConnectedUpStation(addSection, section));
    }

    private void checkConnectedUpStation(Section addSection, Section section) {
        if (section.getUpStation().equals(addSection.getUpStation())) {
            section.connectUpStationToDownStation(addSection);
        }
    }

    private void ifConnectedDownStation(Section addSection) {
        sections.forEach(section -> checkConnectedDownStation(addSection, section));
    }

    private void checkConnectedDownStation(Section addSection, Section section) {
        if (section.getDownStation().equals(addSection.getDownStation())) {
            section.connectDownStationToUpStation(addSection);
        }
    }

    public List<Station> stationsBySorted() {
        return sortStations(findFirstUpStation(), stationsMap());
    }

    private List<Station> sortStations(Station firstUpStation, Map<Station, Station> stationMap) {
        List<Station> stations = new LinkedList<>();
        stations.add(firstUpStation);
        Station upStation = firstUpStation;
        while (stationMap.get(upStation) != null) {
            upStation = stationMap.get(upStation);
            stations.add(upStation);
        }
        return stations;
    }

    private Station findFirstUpStation() {
        Set<Station> downStations = downStationsSet();
        return sections.stream()
                .map(Section::getUpStation)
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(UP_STATION_NOT_FOUND.getMessage()));
    }

    private Set<Station> downStationsSet() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
    }

    private Map<Station, Station> stationsMap() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    public void deleteSection(Station station) {
        validDeleteStation(station);
        Section upSection = findUpStationSection(station).orElse(null);
        Section downSection = findDownStationSection(station).orElse(null);

        // TODO: 종점에 있는 경우는 그냥 삭제
        if (upSection == null) {
            sections.remove(downSection);
            return;
        }
        if (downSection == null) {
            sections.remove(upSection);
            return;
        }
        sectionConnectChange(upSection, downSection);

        // TODO: 중간 구역을 삭제하는 경우 기존 상행 하행 연결 필요

    }

    private void sectionConnectChange(Section upSection, Section downSection) {
    }

    private void validDeleteStation(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException(ONE_SECTION_NOT_DELETE.getMessage());
        }

        if (!distinctStations().contains(station)) {
            throw new IllegalArgumentException(STATION_NOT_CONTAINS_NOT_DELETE.getMessage());
        }
    }

    private Optional<Section> findUpStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst();
    }
    private Optional<Section> findDownStationSection(Station station) {
        return sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findFirst();
    }

}
