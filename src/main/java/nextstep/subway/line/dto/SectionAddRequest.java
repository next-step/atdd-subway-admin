package nextstep.subway.line.dto;

public class SectionAddRequest {

    private final String upStationId;
    private final String downStationId;
    private final int distance;

    private SectionAddRequest(String upStationId, String downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionAddRequest of(String upStationId, String downStationId, int distance) {
        return new SectionAddRequest(upStationId, downStationId, distance);
    }

    public static SectionAddRequest of(Long upStationId, Long downStationId, int distance) {
        return new SectionAddRequest(String.valueOf(upStationId), String.valueOf(downStationId), distance);
    }

    public String getUpStationId() {
        return upStationId;
    }

    public String getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
