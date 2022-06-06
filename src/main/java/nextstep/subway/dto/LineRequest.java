package nextstep.subway.dto;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;

    private String color;

    private long upStationId;

    private long downStationId;

    private long distance;

    protected LineRequest(final String name, final String color, final long upStationId, final long downStationId,
                          final long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineRequest of(final String name, final String color, final long upStationId,
                                 final long downStationId,
                                 final long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }

    public Line toLine(final Station upStation, final Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }
}
