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
    private static final String ONE_MUST_MATCH_EXCEPTION = "상/하행선 둘 중 하나는 일치해야 합니다.";
    private static final String SAME_SECTION_ADD_EXCEPTION = "동일한 구간은 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        boolean isUpStationExist = isStationExist(section.upStation());
        boolean isDownStationExist = isStationExist(section.downStation());
        validSection(isUpStationExist, isDownStationExist);

        if (isUpStationExist) {
            updateUpStation(section);
        }

        if (isDownStationExist) {
            updateDownStation(section);
        }
        sections.add(section);
    }

    public List<Station> assembleStations() {
        if (sections().isEmpty()) {
            return Arrays.asList();
        }
        return orderSection();
    }

    private List<Station> orderSection() {
        List<Station> stations = new ArrayList<>();
        Station station = findFirstSection();
        stations.add(station);

        while (isAfterSection(station)) {
            Section afterStation = findAfterSection(station);
            station = afterStation.downStation();
            stations.add(station);
        }
        return stations;
    }

    private List<Section> sections() {
        return sections;
    }

    private void updateUpStation(Section section) {
        Station inputUpStation = section.upStation();
        sections.stream()
                .filter(it -> it.isEqualsUpStation(inputUpStation))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section));
    }

    private void updateDownStation(Section section) {
        Station inputDownStation = section.downStation();
        sections.stream()
                .filter(it -> it.isEqualsDownStation(inputDownStation))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section));
    }

    private boolean isBeforeSection(Station station) {
        return sections.stream()
                .anyMatch(it -> it.isEqualsDownStation(station));
    }

    private boolean isAfterSection(Station station) {
        return sections.stream()
                .anyMatch(it -> it.isEqualsUpStation(station));
    }

    private Station findFirstSection() {
        Station station = sections.get(0).upStation();
        while (isBeforeSection(station)) {
            Section section = findBeforeSection(station);
            station = section.upStation();
        }
        return station;
    }

    private Section findBeforeSection(Station station) {
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
        return assembleStations().stream()
                .anyMatch(it -> it == station);
    }

    private void validSection(boolean isUpStationExist, boolean isDownStationExist) {
        if (!isUpStationExist && !isDownStationExist && !assembleStations().isEmpty()) {
            throw new RuntimeException(ONE_MUST_MATCH_EXCEPTION);
        }
        if (isUpStationExist && isDownStationExist) {
            throw new RuntimeException(SAME_SECTION_ADD_EXCEPTION);
        }
    }
}
