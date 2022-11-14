package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;

    private int distance;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, int distance, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
    }

    public static LineResponse of(Line line, List<StationResponse> stations) {
        return new LineResponse(
                line.getId(), line.getName(), line.getColor(), line.getDistance(), stations
        );
    }
}
