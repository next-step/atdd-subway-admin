package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

import java.time.LocalDateTime;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private SectionResponse(Long id,
                            Long lineId,
                            Long upStationId,
                            Long downStationId,
                            int distance,
                            LocalDateTime createdDate,
                            LocalDateTime modifiedDate) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(
                section.getId(),
                section.lineId(),
                section.upStationId(),
                section.downStationId(),
                section.distance(),
                section.getCreatedDate(),
                section.getModifiedDate());
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
