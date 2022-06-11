package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class LineAddRequest {
    private final String name;
    private final String color;
    private final long upStationId;
    private final long downStationId;
    private final long distance;

    public LineAddRequest(final String name, final String color, final long upStationId,
                          final long downStationId, final long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toEntity(final Station upStation, final Station downStation) {
        return Line.of(name, color, distance, upStation, downStation);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getDistance() {
        return distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    @Override
    public String toString() {
        return "LineAddRequest{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
