package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineSectionCreateRequest {

    private Line line;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private boolean start;

    public LineSectionCreateRequest(Line line, Long upStationId, Long downStationId, int distance, boolean start) {
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.start = start;
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

    public boolean isStart() {
        return start;
    }
}
