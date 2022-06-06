package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

public class LineResponse {

    private long id;

    private String name;

    private String color;

    private List<StationResponse> stations;

    protected LineResponse(final long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(final Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.stations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList())
        );
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
