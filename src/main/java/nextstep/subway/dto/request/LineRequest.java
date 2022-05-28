package nextstep.subway.dto.request;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.enums.LineColor;

public class LineRequest {

    private Long id;
    private String name;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private String color;

    public Line toLine(Station upStation, Station downStation) {
        return new Line(id, name, LineColor.getLineColorByName(color), upStation, downStation);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
