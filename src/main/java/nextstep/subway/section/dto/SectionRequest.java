package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionRequest {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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

    public Section toSection() {
        return new Section(id, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionRequest{" +
            "id=" + id +
            ", lineId=" + lineId +
            ", upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            ", distance=" + distance +
            '}';
    }
}
