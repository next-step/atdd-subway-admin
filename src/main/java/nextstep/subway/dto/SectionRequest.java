package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SectionRequest {
    private long previousStationId = 0L;
    private long nextStationId = 0L;
    private long stationId;
    private int distance;

    @JsonCreator
    public SectionRequest(
            @JsonProperty("previousStationId") Long previousStationId,
            @JsonProperty("nextStationId") Long nextStationId,
            @JsonProperty("stationId") Long stationId,
            @JsonProperty("distance") Integer distance) {
        if (distance == null) {
            throw new IllegalArgumentException("distance 는 null 일 수 없습니다.");
        }
        if (stationId == null) {
            throw new IllegalArgumentException("stationId 는 null 일 수 없습니다.");
        }

        this.stationId = stationId;
        this.distance = distance;

        if (Objects.nonNull(previousStationId)) {
            this.previousStationId = previousStationId;
        }
        if (Objects.nonNull(nextStationId)) {
            this.nextStationId = nextStationId;
        }
    }

    public Long getPreviousStationId() {
        return previousStationId;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
