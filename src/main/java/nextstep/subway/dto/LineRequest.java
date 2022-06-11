package nextstep.subway.dto;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private int distance;

    protected LineRequest(final String name, final String color, final Long upStationId, final Long downStationId,
                          final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineRequest of(final String name, final String color, final Long upStationId,
                                 final Long downStationId,
                                 final int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
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
        return new Line(name, color);
    }
}
