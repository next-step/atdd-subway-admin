package nextstep.subway.line.dto;

import java.util.Objects;

/**
 * packageName : nextstep.subway.line.dto
 * fileName : SectionRequest
 * author : haedoang
 * date : 2021-11-23
 * description :
 */
public class SectionRequest implements BaseRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    private SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    @Override
    public boolean hasDuplicateStations() {
        return Objects.equals(upStationId, downStationId);
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
