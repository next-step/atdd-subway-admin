package nextstep.subway.section.dto;

import com.sun.nio.sctp.IllegalReceiveException;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SectionRequest {
    private static final String NOT_EXIST_STATIONS_ERROR = "추가하려는 구간의 상행역과 하행역 모두 선택해야 합니다.";
    private static final String INVALID_DISTANCE_VALUE_ERROR = "추가되는 구간의 거리는 0이 될 수 없습니다.";
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected SectionRequest() {
    }

    @Builder
    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        validateStations(upStationId, downStationId);
        validateDistance(distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateStations(Long upStationId, Long downStationId) {
        if(Objects.isNull(upStationId) && Objects.isNull(downStationId)){
            throw new IllegalArgumentException(NOT_EXIST_STATIONS_ERROR);
        }
    }

    private void validateDistance(int distance) {
        if(distance == 0) {
            throw new IllegalReceiveException(INVALID_DISTANCE_VALUE_ERROR);
        }
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
