package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

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

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    public Line toLine(final Station upStation, final Station downStation) {
        return new Line(this, upStation, downStation);
    }
}
