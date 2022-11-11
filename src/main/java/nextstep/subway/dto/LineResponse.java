package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList();

    public static LineResponse of(Line line) {
        return new LineResponse(line);
    }

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();;
        this.name = line.getName();
        this.color = line.getColor();
        this.stations.add(StationResponse.of(line.getUpStation()));
        this.stations.add(StationResponse.of(line.getDownStation()));
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


}
