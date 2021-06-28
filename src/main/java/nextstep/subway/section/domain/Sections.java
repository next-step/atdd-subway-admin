package nextstep.subway.section.domain;

import nextstep.subway.section.exception.UnaddableSectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> values = new ArrayList<>();

    public void add(Section section) {
        validateConnectableSection(section);
        addSectionByCase(section);
    }

    public List<Station> toStations() {
        List<Station> result = new ArrayList<>();
        if (!isEmpty()) {
            Section section = values.get(0);
            result.addAll(getUpStations(section.getUpStation()));
            result.addAll(getDownStations(section.getDownStation()));
        }
        return result;
    }

    private boolean isEmpty() {
        return values.isEmpty();
    }

    private void validateConnectableSection(Section section) {
        final int ILLEGAL_OVERLAPPED_STATION_COUNT = 2;
        final int ILLEGAL_UN_CONNECTABLE_STATION_COUNT = 0;

        long result = getConnectedStationCount(section);

        if (result == ILLEGAL_OVERLAPPED_STATION_COUNT) {
            throw new UnaddableSectionException("기존에 등록된 구간과 중복됩니다.");
        } else if (!isEmpty() && result == ILLEGAL_UN_CONNECTABLE_STATION_COUNT) {
            throw new UnaddableSectionException("요청하신 구간은 연결이 불가합니다.");
        }
    }

    private long getConnectedStationCount(Section section) {
        List<Station> stations = toStations();

        return stations.stream()
                .filter(station -> station.equals(section.getUpStation())
                        || station.equals(section.getDownStation()))
                .count();
    }

    private void addSectionByCase(Section section) {
        if (isEmpty() || isStartOrEndStationInLine(section)) {
            this.values.add(section);
            return;
        }

        if (isOverlappedUpStationBetweenSection(section)) {
            connectUpStationBetweenSection(section);
            return;
        }

        if (isOverlappedDownStationBetweenSection(section)) {
            connectDownStationBetweenSection(section);
            return;
        }
    }

    private boolean isStartOrEndStationInLine(Section section) {
        List<Station> stations = toStations();
        Station startStation = stations.get(0);
        Station endStation = stations.get(stations.size() - 1);

        return section.isSameStationWithDownStation(startStation)
                || section.isSameStationWithUpStation(endStation);
    }

    private boolean isOverlappedUpStationBetweenSection(Section section) {
        final int ILLEGAL_OVERLAPPED_UP_STATION_COUNT = 1;
        Station upStation = section.getUpStation();

        return values.stream()
                .filter(value -> value.isSameStationWithUpStation(upStation))
                .count() >= ILLEGAL_OVERLAPPED_UP_STATION_COUNT;
    }

    private boolean isOverlappedDownStationBetweenSection(Section section) {
        final int ILLEGAL_OVERLAPPED_DOWN_STATION_COUNT = 1;
        Station downStation = section.getDownStation();

        return values.stream()
                .filter(value -> value.isSameStationWithDownStation(downStation))
                .count() >= ILLEGAL_OVERLAPPED_DOWN_STATION_COUNT;
    }

    private void connectUpStationBetweenSection(Section section) {
        Station upStation = section.getUpStation();

        Optional<Section> foundSection = values.stream()
                .filter(value -> value.isSameStationWithUpStation(upStation)).findFirst();

        if (foundSection.isPresent()) {
            Section connectedSection = foundSection.get();
            connectedSection.replaceUpStation(section);
            values.add(section);
        }
    }

    private void connectDownStationBetweenSection(Section section) {
        Station downStation = section.getDownStation();

        Optional<Section> foundSection = values.stream()
                .filter(value -> value.isSameStationWithDownStation(downStation)).findFirst();

        if (foundSection.isPresent()) {
            Section connectedSection = foundSection.get();
            connectedSection.replaceDownStation(section);
            values.add(section);
        }
    }

    private List<Station> getUpStations(Station upStation) {
        LinkedList<Station> result = new LinkedList<>();
        Station foundUpStation = upStation;
        while (foundUpStation != null) {
            result.addFirst(foundUpStation);
            foundUpStation = findUpStation(foundUpStation).orElse(null);
        }
        return result;
    }

    private List<Station> getDownStations(Station downStation) {
        List<Station> result = new ArrayList<>();
        Station foundDownStation = downStation;
        while (foundDownStation != null) {
            result.add(foundDownStation);
            foundDownStation = findDownStation(foundDownStation).orElse(null);
        }
        return result;
    }

    private Optional<Station> findUpStation(Station upStation) {
        return values.stream()
                .filter(section -> section.getDownStation().getId().equals(upStation.getId()))
                .map(Section::getUpStation)
                .findFirst();
    }

    private Optional<Station> findDownStation(Station downStation) {
        return values.stream()
                .filter(section -> section.getUpStation().getId().equals(downStation.getId()))
                .map(Section::getDownStation)
                .findFirst();
    }
}
