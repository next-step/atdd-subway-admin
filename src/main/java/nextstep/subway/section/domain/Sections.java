package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    private static final String ONE_MUST_MATCH_EXCEPTION = "상/하행선 둘 중 하나는 일치해야 합니다.";
    private static final String SAME_SECTION_ADD_EXCEPTION = "동일한 구간은 추가할 수 없습니다.";
    private static final String CANNOT_DELETE_EXCEPTION = "역을 제거할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
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
        Station station = findFirstStation();
        stations.add(station);

        while (isAfterSection(station)) {
            Optional<Section> afterStation = findAfterSection(station);
            station = afterStation.get().downStation();
            stations.add(station);
        }
        return stations;
    }

    public void deleteSection(Line line, Station station) {
        validDeleteSection(station);
        Optional<Section> upSection = findBeforeSection(station);
        Optional<Section> downSection = findAfterSection(station);

        createSection(line, upSection, downSection);
        deleteUpOrDownSection( upSection, downSection);
    }

    private List<Section> sections() {
        return sections;
    }

    private void updateUpStation(Section inputSection) {
        Station inputUpStation = inputSection.upStation();
        sections.stream()
                .filter(section -> section.isEqualsUpStation(inputUpStation))
                .findFirst()
                .ifPresent(section -> section.updateUpStation(inputSection));
    }

    private void updateDownStation(Section inputSection) {
        Station inputDownStation = inputSection.downStation();
        sections.stream()
                .filter(section -> section.isEqualsDownStation(inputDownStation))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(inputSection));
    }

    private boolean isBeforeSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isEqualsDownStation(station));
    }

    private boolean isAfterSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isEqualsUpStation(station));
    }

    private Station findFirstStation() {
        Station station = sections.get(0).upStation();
        while (isBeforeSection(station)) {
            Optional<Section> section = findBeforeSection(station);
            station = section.get().upStation();
        }
        return station;
    }

    private Optional<Section> findBeforeSection(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsDownStation(station))
                .findFirst();
    }

    private Optional<Section> findAfterSection(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst();
    }

    public boolean isStationExist(Station inputStation) {
        return assembleStations().stream()
                .anyMatch(station -> station.equals(inputStation));
    }

    private void validSection(boolean isUpStationExist, boolean isDownStationExist) {
        if (!isUpStationExist && !isDownStationExist && !assembleStations().isEmpty()) {
            throw new RuntimeException(ONE_MUST_MATCH_EXCEPTION);
        }
        if (isUpStationExist && isDownStationExist) {
            throw new RuntimeException(SAME_SECTION_ADD_EXCEPTION);
        }
    }

    private void createSection(Line line, Optional<Section> upSection, Optional<Section> downSection) {
        if (upSection.isPresent() && downSection.isPresent()) {
            Station upStation = upSection.get().upStation();
            Station  downStation = downSection.get().downStation();
            int distance = upSection.get().distance() + downSection.get().distance();
            sections.add(new Section(line, upStation, downStation, distance));
        }
    }

    private void deleteUpOrDownSection(Optional<Section> upSection, Optional<Section> downSection) {
        upSection.ifPresent(section -> sections.remove(section));
        downSection.ifPresent(section -> sections.remove(section));
    }

    private void validDeleteSection(Station station) {
        if (!isStationExist(station) || sections.size() == 1) {
            throw new RuntimeException(CANNOT_DELETE_EXCEPTION);
        }
    }
}
