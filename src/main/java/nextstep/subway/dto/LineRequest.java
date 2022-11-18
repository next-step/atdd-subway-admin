package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private int distance;

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public int getDistance() {
        return this.distance;
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(this.name, this.color, upStation, downStation, distance);
    }
}
