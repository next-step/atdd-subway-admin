package nextstep.subway.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
    }

    private void addSection(Section section) {
        this.sections.add(section);
        section.addLine(this);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, Section.of(upStation, downStation, distance));
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

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
