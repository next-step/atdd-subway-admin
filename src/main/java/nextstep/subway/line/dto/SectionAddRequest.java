package nextstep.subway.line.dto;

import java.util.Objects;

public class SectionAddRequest {
    private final Long downStationId;
    private final Long upStationId;
    private final long distance;

    public SectionAddRequest(final Long downStationId, final Long upStationId, final long distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
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
                "downStationId=" + downStationId +
                ", upStationId=" + upStationId +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SectionAddRequest that = (SectionAddRequest) o;
        return distance == that.distance && Objects.equals(downStationId, that.downStationId) && Objects.equals(upStationId, that.upStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(downStationId, upStationId, distance);
    }
}
