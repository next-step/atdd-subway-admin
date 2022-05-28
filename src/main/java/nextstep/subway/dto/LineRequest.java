package nextstep.subway.dto;

import java.util.Optional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine(Station downStation, Station upStation) {
        return new Line(name, color, downStation, upStation);
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

}
