package nextstep.subway.dto;

import nextstep.subway.domain.line.Distance;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineColor;
import nextstep.subway.domain.line.LineName;

public class LineCreateRequest {

    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
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

    public Line toLineEntity() {
        return new Line(
                new LineName(this.name),
                new LineColor(this.color),
                new Distance(this.distance)
        );
    }
}
