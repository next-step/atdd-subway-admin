package nextstep.subway.dto;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;


public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> sections = new ArrayList<>();

    public LineResponse() {

    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                sectionToResponse(line.getSections()));

    }

    public LineResponse(Long id, String name, String color, List<SectionResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    private static List<SectionResponse> sectionToResponse(Sections sections) {
        return sections.getList()
                .stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

}
