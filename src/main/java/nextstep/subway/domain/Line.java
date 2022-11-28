package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    Sections sections = new Sections();

    protected Line() {

    }

    public Line(String name) {
        this.name = name;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line of(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        return this;
    }

    public Sections addSection(Section section) {
        return sections.addSection(section);
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

    public Sections getSections() {
        return sections;
    }

}
