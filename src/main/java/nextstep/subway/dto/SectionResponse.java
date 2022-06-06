package nextstep.subway.dto;

import java.time.LocalDateTime;
import nextstep.subway.domain.Section;

public class SectionResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private Integer distance;
    private Integer order;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static SectionResponse from(Section section) {
        return new SectionResponse(section.getId(), StationResponse.from(section.getUpStation()),
                StationResponse.from(section.getDownStation()), section.getDistance(), section.getOrderNumber(),
                section.getCreatedDate(), section.getModifiedDate());
    }

    public SectionResponse() {
    }

    public SectionResponse(Long id, StationResponse upStation, StationResponse downStation, Integer distance,
                           Integer order,
                           LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.order = order;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
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
