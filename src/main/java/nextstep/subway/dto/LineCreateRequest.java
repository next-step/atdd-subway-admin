package nextstep.subway.dto;

import nextstep.subway.domain.Color;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Name;

public class LineCreateRequest {
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() throws IllegalArgumentException {
        return new Line(new Name(name), new Color(color), upStationId, downStationId, new Distance(distance));
    }
}
