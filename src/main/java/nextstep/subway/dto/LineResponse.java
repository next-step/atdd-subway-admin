package nextstep.subway.dto;

import static java.util.stream.Collectors.toList;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> sectionResponses;

    public LineResponse() {
    }

    private LineResponse(Long id, String name, String color, List<SectionResponse> sectionResponses) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sectionResponses = sectionResponses;
    }

    public static LineResponse of(Line line) {
        Sections sections = line.getSections();
        List<SectionResponse> sectionResponses = sections.getSectionList()
                .stream()
                .map(SectionResponse::of)
                .collect(toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionResponses);
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

    public List<SectionResponse> getSectionResponses() {
        return sectionResponses;
    }
}
