package nextstep.subway.dto;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    List<StationResponse> stations;
    private Long distance;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations,
        Long distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.distance = distance;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            Arrays.asList(StationResponse.of(line.getUpStation()),
                StationResponse.of(line.getDownStation())),
            line.getDistance());
    }


    public Long getId() {
        return id;
    }
}
