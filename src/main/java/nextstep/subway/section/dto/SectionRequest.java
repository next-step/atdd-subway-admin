package nextstep.subway.section.dto;

import com.sun.istack.NotNull;
import nextstep.subway.section.domain.Section;

public class SectionRequest {
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public void validate(Long upStationId, Long downStationId, int distance) {
        if (upStationId <= 0 || downStationId <= 0 || upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("잘못된 Section Request 입니다. "
                + String.format("upStationId = %d, downStationId = %d", upStationId, downStationId));
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("잘못된 Section Request 입니다. "
                + String.format("distance = %d", distance));
        }
    }
}
