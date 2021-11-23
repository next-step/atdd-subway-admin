package nextstep.subway.line.dto;

public class SectionRequest {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(LineRequest lineRequest) {
        validateLineRequestArguments(lineRequest);
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    private static void validateLineRequestArguments(LineRequest lineRequest) {
        if (lineRequest.getUpStationId() == null) {
            throw new IllegalArgumentException("upStationId가 필요합니다.");
        }
        if (lineRequest.getDownStationId() == null) {
            throw new IllegalArgumentException("downStationId가 필요합니다.");
        }
        if (lineRequest.getDistance() == 0) {
            throw new IllegalArgumentException("distance가 필요합니다.");
        }
    }

    public Long getId() {
        return id;
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
