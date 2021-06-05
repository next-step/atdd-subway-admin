package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = Sections.of(new ArrayList<>());

    public Line() { }

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.sections = line.getSections();
    }

    public void addSection(Section section){
        this.sections.getValues().add(section);
        section.toLine(this);
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
