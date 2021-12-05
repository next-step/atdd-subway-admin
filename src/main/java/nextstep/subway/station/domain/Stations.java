package nextstep.subway.station.domain;

import nextstep.subway.station.dto.StationResponse;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Stations {

    private List<Station> stations = new ArrayList<>();

    public Station getFirstStation() {
        validateStations();
        return stations.get(0);
    }

    public Station getLastStation() {
        validateStations();
        return stations.get(stations.size() - 1);
    }

    public List<StationResponse> toResponse() {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public boolean add(Station station) {
        if (notContains(station)) {
            return stations.add(station);
        }
        return false;
    }

    public void removeAll(Stations stations) {
        this.stations.removeAll(stations.stations);
    }

    public boolean contains(Station station) {
        return stations.contains(station);
    }

    public boolean notContains(Station station) {
        return !contains(station);
    }

    public boolean isEmpty() {
        return stations.isEmpty();
    }

    private void validateStations() {
        if (CollectionUtils.isEmpty(stations)) {
            throw new NoSuchElementException("역 목록이 비어있습니다.");
        }
    }
}
