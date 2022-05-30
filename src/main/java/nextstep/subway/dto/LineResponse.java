package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private final long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stationResponses = new ArrayList<>();
        stationResponses.add(toStationResponse(line.getUpStation()));
        stationResponses.add(toStationResponse(line.getDownStation()));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    private static StationResponse toStationResponse(Station station) {
        return StationResponse.of(station);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
