package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

import javax.persistence.*;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {}

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line updateByLineRequest(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        return this;
    }

    public void infixSection(Section section) {
        sections.infix(section);
    }

    public LineResponse toLineResponse() {
        return new LineResponse(id, name, color, sections);
    }

    public Long getId() {
        return id;
    }


}
