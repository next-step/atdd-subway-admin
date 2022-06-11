package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.Stations;

import java.util.List;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final Stations stations;

    public LineResponse(final Long id, final String name, final String color, final Stations stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                Stations.of(line.getFinalUpStation(), line.getFinalDownStation())
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations.getStations();
    }
}
