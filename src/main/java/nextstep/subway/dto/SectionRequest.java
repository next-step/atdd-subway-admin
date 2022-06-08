package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SectionRequest {
    private long upStationId;
    private long downStationId;
    private int distance;

    @JsonCreator
    public SectionRequest(@JsonProperty("upStationId") long upStationId,
            @JsonProperty("downStationId") long downStationId,
            @JsonProperty("distance") int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
