package nextstep.subway.line.dto;

import lombok.Builder;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Section;


@Builder
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downstationId;
    private Long distance;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownstationId() {
        return downstationId;
    }

    public Long getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(this);
    }
    public Line toLine(Section section) {
        return new Line(this,section);
    }
}
