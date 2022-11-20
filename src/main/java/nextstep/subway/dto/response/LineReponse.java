package nextstep.subway.dto.response;

import nextstep.subway.domain.line.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineReponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int distance;

    public static LineReponse of(Line line) {
        return new LineReponse(line.getId(), line.getName(), line.getColor()
                                    , line.getStations().stream()
                                            .map(StationResponse::of)
                                            .collect(Collectors.toList()), line.getDistance());
    }

    protected LineReponse() {
    }

    public LineReponse(Long id, String name, String color, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public LineReponse(Long id, String name, String color, List<StationResponse> stations, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }
}
