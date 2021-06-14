package nextstep.subway.section.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SectionResponse {
    private Long id;
    private String upStation;
    private Long lineId;
    private String downStation;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    private SectionResponse(final Long id, final String upStation, final Long lineId, final String downStation, final int distance, final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.upStation = upStation;
        this.lineId = lineId;
        this.downStation = downStation;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(final Section section) {
        return SectionResponse.builder()
            .id(section.getId())
            .lineId(section.getLine().getId())
            .upStation(section.getUpStation().getName())
            .downStation(section.getDownStation().getName())
            .distance(section.getDistance())
            .createdDate(section.getCreatedDate())
            .modifiedDate(section.getModifiedDate())
            .build();
    }
}
