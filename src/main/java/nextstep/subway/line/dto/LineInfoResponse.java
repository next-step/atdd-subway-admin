package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;

public class LineInfoResponse {

    private Long id;
    private String name;
    private String color;
    private List<Section> sections;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineInfoResponse() {
    }

    public LineInfoResponse(Long id, String name, String color, List<Section> sections, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineInfoResponse of(Line line) {
        return new LineInfoResponse(line.getId(), line.getName(), line.getColor(), line.getSections(), line.getCreatedDate(), line.getModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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
