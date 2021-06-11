package nextstep.subway.section.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.line.dto.LineResponse;

import java.time.LocalDateTime;

public class SectionResponse {
    private Long id;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private LineResponse lineResponse;

    public SectionResponse(Long id, int distance, LocalDateTime createdDate,
                           LocalDateTime modifiedDate, LineResponse lineResponse) {
        this.id = id;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.lineResponse = lineResponse;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.id(), section.distance(), section.createdDate(),
                section.modifiedDate(), LineResponse.of(section.line()));
    }

    public Long getId() {
        return id;
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

    public LineResponse getLineResponse() {
        return lineResponse;
    }
}
