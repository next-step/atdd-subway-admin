package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import nextstep.subway.domain.Line;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest(String name, String color, long upStationId, long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineRequest of(String name, String color, long upStationId, long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public Line toLine() {
        return new Line(name, color, upStationId, downStationId, distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
