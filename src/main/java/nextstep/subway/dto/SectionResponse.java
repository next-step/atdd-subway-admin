package nextstep.subway.dto;

import java.time.LocalDateTime;

public class SectionResponse {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private Integer order;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse() {
    }

    public SectionResponse(Long id, Long upStationId, Long downStationId, Integer distance, Integer order,
                           LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.order = order;
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

    public Integer getDistance() {
        return distance;
    }

    public Integer getOrder() {
        return order;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
