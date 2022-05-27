package nextstep.subway.line.dto;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color,
                        List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Long id, String name, String color,
                               List<StationResponse> stations) {
        return new LineResponse(id, name, color, stations);
    }

    public static LineResponse of(Line line) {
        return LineResponse.of(line.getId(), line.getName(), line.getColor(),
                Arrays.asList(
                        StationResponse.of(line.getUpStation()),
                        StationResponse.of(line.getDownStation())
                )
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
        return stations;
    }
}
