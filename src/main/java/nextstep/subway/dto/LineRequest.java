package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private final String name;
    private final String color;
    private final int distance;
    private final Long upStationId;
    private final Long downStationId;

    public LineRequest(String name, String color, int distance, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine(Station upStation, Station downStation) {
        return Line.builder()
                .name(this.name)
                .color(this.color)
                .distance(this.distance)
                .upStation(upStation)
                .downStation(downStation).build();
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
