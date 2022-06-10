package nextstep.subway.dto.line;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;

public class CreateLineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private CreateLineRequest() {
    }

    public CreateLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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

    public Line toEntity(Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }
}
