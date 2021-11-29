package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nextstep.subway.section.domain.Section;

public class Stations {
    private final List<Station> stations;

    public Stations(Map<Station, Station> stationPath) {
        this.stations = makeStations(stationPath);
    }

    private List<Station> makeStations(Map<Station, Station> stationPath) {
        Optional<Station> upperMost = findUpperMost(stationPath);

        if (!upperMost.isPresent()) {
            return new ArrayList<>();
        }

        List<Station> stations = makeStations(stationPath, upperMost.get());
        return Collections.unmodifiableList(stations);
    }

    private Optional<Station> findUpperMost(Map<Station, Station> stationPath) {
        return stationPath.keySet()
            .stream()
            .filter(upStation -> !stationPath.containsValue(upStation))
            .findFirst();
    }

    private List<Station> makeStations(Map<Station, Station> stationPath, Station upperMost) {
        List<Station> stations = new ArrayList<>();

        Station station = upperMost;
        while (station != null) {
            stations.add(station);
            station = stationPath.get(station);
        }

        return stations;
    }

    public void validateSection(Section added) {
        boolean containsDownStation = stations.contains(added.getDownStation());
        boolean containsUpStation = stations.contains(added.getUpStation());

        if (containsDownStation && containsUpStation) {
            throw new IllegalArgumentException("이미 모두 구간에 포함되어 있습니다.");
        }

        if (!containsDownStation && !containsUpStation) {
            throw new IllegalArgumentException("모두 구간에 포함되어 있지 않습니다.");
        }
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }
}
