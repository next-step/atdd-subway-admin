package nextstep.subway.dto;


import static java.util.Objects.isNull;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionRequest() {

    }

    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDistance() {
        return distance;
    }

    public void validate() {
        if (isNull(upStationId)) {
            throw new IllegalArgumentException("노선 구간의 상행역이 정상적으로 입력되지 않았습니다.");
        }
        if (isNull(downStationId)) {
            throw new IllegalArgumentException("노선 구간의 하행역이 정상적으로 입력되지 않았습니다.");
        }
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("노선 구간의 상행역과 하앵역이 동일할 수 없습니다.");
        }
    }
}
