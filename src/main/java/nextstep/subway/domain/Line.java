package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private LineName name;
    @Embedded
    private LineColor color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
        addSection(new Section.Builder(upStation, downStation, distance).build());
    }

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public void update(Line newLine) {
        this.name = newLine.getName();
        this.color = newLine.getColor();
    }

    public Long getId() {
        return id;
    }

    public LineName getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }
}
