package nextstep.subway.dto;

import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Section;

import java.time.LocalDateTime;
import java.util.List;

public class SectionResponse {
    private final Long id;

    private final Long lineId;
    private final List<Section> sections;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public SectionResponse(Long id, Long lineId, List<Section> sections, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.lineId = lineId;
        this.sections = sections;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(final LineStation lineStation) {
        return new SectionResponse(lineStation.getId(),
                lineStation.getLine().getId(),
                lineStation.getLine().getLineStations().getSections(),
                lineStation.getCreatedDate(),
                lineStation.getModifiedDate()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public List<Section> getSections() {
        return sections;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
