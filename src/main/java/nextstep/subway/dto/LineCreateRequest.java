package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineCreateRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    public Line toEntity() {
        return new Line();
    }
}
