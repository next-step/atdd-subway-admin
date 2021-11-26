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

    @Override
    public Long getDownStationId() {
        return downStationId;
    }

    @Override
    public Long getUpStationId() {
        return upStationId;
    }

    @Override
    public int getDistance() {
        return distance;
    }
}
