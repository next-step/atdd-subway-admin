package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.domain.Sections;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<SectionResponse> sections;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.sections = sections.toSectionResponses();
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(), line.getSections());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }

    public String getColor() {
        return color;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
