package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineStations {
    private final List<Station> lineStations;

    private LineStations(List<Station> lineStations) {
        this.lineStations = distinctStations(lineStations);
    }

    public static LineStations from(List<Station> lineStations) {
        return new LineStations(lineStations);
    }

    public boolean contains(Station station) {
        return this.lineStations.contains(station);
    }

    public int indexOf(Station station) {
        return this.lineStations.indexOf(station);
    }

    public List<StationResponse> toStationResponses() {
        return this.lineStations.stream()
                .map(Station::toStationResponse)
                .collect(Collectors.toList());
    }

    private List<Station> distinctStations(List<Station> allStations) {
        return allStations.stream().distinct().collect(Collectors.toList());
    }
}
