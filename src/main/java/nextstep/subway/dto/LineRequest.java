package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStatsionId;
    private long distance;

    public Line toLine() {
        return new Line(name, color);
    }
}
