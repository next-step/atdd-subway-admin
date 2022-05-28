package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Sections;

import javax.persistence.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private LineName name;
    @Embedded
    private LineColor color;
    @Embedded
    private Sections sections;

    protected Line() {}

    public Line(LineName name, LineColor color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(LineName.from(name), LineColor.from(color));
    }
}
