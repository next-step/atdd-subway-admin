package nextstep.subway.section.dto;

import java.time.LocalDateTime;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private Long lineId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse() {
    }

    public SectionResponse(Long id, Long upStationId, Long downStationId, int distance,
        Long lineId, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
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

    public Long getLineId() {
        return lineId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(),
            section.getUpStation().getId(),
            section.getDownStation().getId(),
            section.getDistance(),
            section.getLine().getId(),
            section.getCreatedDate(),
            section.getModifiedDate());
    }
}
