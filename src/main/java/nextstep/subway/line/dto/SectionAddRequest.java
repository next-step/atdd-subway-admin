package nextstep.subway.line.dto;

import java.util.Objects;

public class SectionAddRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final long distance;

    public SectionAddRequest(final Long upStationId, final Long downStationId, final long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "SectionAddRequest{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SectionAddRequest that = (SectionAddRequest) o;
        return distance == that.distance && Objects.equals(upStationId, that.upStationId) && Objects.equals(downStationId, that.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }
}
