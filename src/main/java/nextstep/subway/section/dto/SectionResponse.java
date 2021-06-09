package nextstep.subway.section.dto;

import java.util.Objects;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {
    private Long id;
    private StationResponse upStationResponse;
    private StationResponse downStationResponse;
    private int distance;
    private Long lineId;

    public SectionResponse() {
    }

    public SectionResponse(Long id, StationResponse upStationResponse, StationResponse downStationResponse,
                           int distance, Long lineId) {
        this.id = id;
        this.upStationResponse = upStationResponse;
        this.downStationResponse = downStationResponse;
        this.distance = distance;
        this.lineId = lineId;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation()), section.getDistance(), section.getLine().getId());
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStationResponse() {
        return upStationResponse;
    }

    public StationResponse getDownStationResponse() {
        return downStationResponse;
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }

    @Override
    public String toString() {
        return "SectionResponse{" +
                "id=" + id +
                ", upStationResponse=" + upStationResponse +
                ", downStationResponse=" + downStationResponse +
                ", distance=" + distance +
                ", lineId=" + lineId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionResponse that = (SectionResponse) o;
        return distance == that.distance &&
                Objects.equals(id, that.id) &&
                Objects.equals(upStationResponse, that.upStationResponse) &&
                Objects.equals(downStationResponse, that.downStationResponse) &&
                Objects.equals(lineId, that.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStationResponse, downStationResponse, distance, lineId);
    }
}
