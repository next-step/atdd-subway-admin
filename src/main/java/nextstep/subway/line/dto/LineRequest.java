package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;

import java.util.Arrays;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected LineRequest() {
    }

    public LineRequest(String name, String color) {
        this(name, color, null, null, 0);
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        validateLineRequest(name, color);
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateLineRequest(String name, String color) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name값은 null 또는 빈값을 넣으면 안됩니다.");
        }
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException("color값은 null 또는 빈값을 넣으면 안됩니다.");
        }
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color, new Section(upStationId, downStationId, distance));
    }
}
