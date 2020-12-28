package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;

public class SectionCreateRequest {

    private Line line;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionCreateRequest(Line line, Long upStationId, Long downStationId, int distance) {
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line getLine() {
        return line;
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
