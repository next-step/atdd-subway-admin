package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class LineAddRequest {
    private final String name;
    private final String color;
    private final long upStationId;
    private final long downStationId;
    private final long distance;

    @JsonCreator
    public LineAddRequest(@JsonProperty("name") final String name,
                          @JsonProperty("color") final String color,
                          @JsonProperty("upStationId") final long upStationId,
                          @JsonProperty("downStationId") final long downStationId,
                          @JsonProperty("distance") final long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toEntity(final Station upStation, final Station downStation) {
        return Line.of(name, color, upStation, downStation, distance);
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
