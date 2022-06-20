package nextstep.subway.dto;

import nextstep.subway.domain.Section;

import java.time.LocalDateTime;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse(Long id, Long lineId, Long upStationId, Long downStationId, Long distance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getLine().getId(), section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance(), section.getCreatedDate(), section.getModifiedDate());
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

    public Long getDistance() {
        return distance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
