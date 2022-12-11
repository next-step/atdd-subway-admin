package nextstep.subway.dto.response;

import nextstep.subway.domain.line.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineReponse {
    private Long id;
    private String name;
    private String color;
    private List<LineStationResponse> stations;

    public static LineReponse of(Line line) {
        return new LineReponse(line.getId(), line.getName(), line.getColor(), line.getLineStations().getValues()
                .stream()
                .map(LineStationResponse::of)
                .collect(Collectors.toList()));
    }

    protected LineReponse() {
    }

    public LineReponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineReponse(Long id, String name, String color, List<LineStationResponse> stations) {
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

    public List<LineStationResponse> getStations() {
        return stations;
    }
}
