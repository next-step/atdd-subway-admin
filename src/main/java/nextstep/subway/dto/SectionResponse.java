package nextstep.subway.dto;

import java.time.LocalDateTime;
import nextstep.subway.domain.Section;

public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private Long lineId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse(Long id, Long upStationId, Long downStationId, int distance, Long lineId,
            LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getLineId(), section.getCreatedDate(), section.getModifiedDate());
    }


    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }
}
