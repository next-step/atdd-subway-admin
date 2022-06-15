package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.LinkedList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> sections;

    private LineResponse() {
    }

    private LineResponse(Long id, String name, String color, List<SectionResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static LineResponse of(Line line) {
        List<SectionResponse> sections = new LinkedList<>();
        line.getAllSections().forEach(section -> sections.add(SectionResponse.of(section)));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), sections);
    }

    public static LineResponse of(Line line, List<SectionResponse> sections) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sections);
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

    public List<SectionResponse> getSections() {
        return sections;
    }
}
