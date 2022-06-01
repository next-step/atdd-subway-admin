package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations = new ArrayList<>();

    public static LineResponse of(final Line line) {
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                stationResponses(line));
    }

    private static List<StationResponse> stationResponses(final Line line) {
        return line.getLineStations().getStations()
                .stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public LineResponse() {
    }

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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
