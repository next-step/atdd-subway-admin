package nextstep.subway.dto.response;

import nextstep.subway.domain.line.Line;

import java.util.List;

public class LineReponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int distance;

    public static LineReponse of(Line line) {
        return new LineReponse(line.getId(), line.getName(), line.getColor(), line.getStationResponses(), line.getDistance());
    }

    public LineReponse() {
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
