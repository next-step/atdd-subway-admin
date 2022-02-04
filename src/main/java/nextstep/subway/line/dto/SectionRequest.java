package nextstep.subway.line.dto;

public class SectionRequest {

    private long downStationId;

    private long upStationId;

    private int distance;

    public SectionRequest(long downStationId, long upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }
}
