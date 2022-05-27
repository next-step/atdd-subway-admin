package nextstep.subway.dto;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> list) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = list;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = Arrays.asList(StationResponse.of(line.getUpStation()),
            StationResponse.of(line.getDownStation()));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
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
