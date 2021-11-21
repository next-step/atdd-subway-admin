package nextstep.subway.line.dto;

import java.util.Arrays;
import java.util.List;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(name, color);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
        return new Line(name, color, new Section(upStationId, downStationId, new Distance(distance)));
    }

    public List<Long> toStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }

}
