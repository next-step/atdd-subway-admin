package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> sections;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<SectionResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<SectionResponse> getSections() {
        return sections;
    }
}
