package nextstep.subway.section.dto;

import nextstep.subway.section.domain.LineStation;

import java.time.LocalDateTime;

public class SectionResponse {

    private Long id;
    private Long lineId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse() {
    }

    public SectionResponse(Long id, Long lineId, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.lineId = lineId;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(LineStation lineStation) {
        return new SectionResponse(
                lineStation.getId(),
                lineStation.getLine().getId(),
                lineStation.getCreatedDate(),
                lineStation.getModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }
    
}
