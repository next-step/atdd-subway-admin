package nextstep.subway.section.dto;

import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionRequest(Long upStationId, Long downStationId, Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }

    public List<Long> toIds() {
        return Arrays.asList(upStationId, downStationId);
    }
}
