package nextstep.subway.section.domain;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        boolean isUpStationExist = isStationExist(section.getUpStation());
        boolean isDownStationExist = isStationExist(section.getDownStation());
        validSection(isUpStationExist, isDownStationExist);

        if (isUpStationExist) {
            updateUpStation(section);
        }

        if (isDownStationExist) {
            updateDownStation(section);
        }
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void updateUpStation(Section section) {
        Station inputUpStation = section.getUpStation();
        sections.stream()
                .filter(it -> it.isEqualsUpStation(inputUpStation))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section));
    }

    public void updateDownStation(Section section) {
        Station inputDownStation = section.getDownStation();
        sections.stream()
                .filter(it -> it.isEqualsDownStation(inputDownStation))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section));
    }

    public List<Station> orderSection() {
        List<Station> stations = new ArrayList<>();
        Station station = getFirstSection();
        stations.add(station);

        while (isAfterSection(station)) {
            Section afterStation = findAfterSection(station);
            station = afterStation.getDownStation();
            stations.add(station);
        }
        return stations;
    }

    public boolean isBeforeSection(Station station) {
        return sections.stream()
                .anyMatch(it -> it.isEqualsDownStation(station));
    }

    public boolean isAfterSection(Station station) {
        return sections.stream()
                .anyMatch(it -> it.isEqualsUpStation(station));
    }

    public Station getFirstSection() {
        Station station = sections.get(0).getUpStation();
        while (isBeforeSection(station)) {
            Section section = findBeforeSection(station);
            station = section.getUpStation();
        }
        return station;
    }

    public Section findBeforeSection(Station station) {
        return sections.stream()
                .filter(it -> it.isEqualsDownStation(station))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    private Section findAfterSection(Station station) {
        return sections.stream()
                .filter(it -> it.isEqualsUpStation(station))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }


    public boolean isStationExist(Station station) {
        return getStations().stream()
                .anyMatch(it -> it == station);
    }

    public List<Station> getStations() {
        if (getSections().isEmpty()) {
            return Arrays.asList();
        }
        return orderSection();
    }

    public void validSection(boolean isUpStationExist, boolean isDownStationExist) {
        if (!isUpStationExist && !isDownStationExist && !getStations().isEmpty()) {
            throw new RuntimeException("상/하행선 둘 중 하나는 일치해야 합니다.");
        }
        if (isUpStationExist && isDownStationExist) {
            throw new RuntimeException("동일한 구간은 추가할 수 없습니다.");
        }
    }

}
