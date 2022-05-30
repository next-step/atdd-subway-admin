package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Integer distance) {
        this.name = name;
        this.color = color;
        this.addSection(Section.of(upStation, downStation, distance));
    }

    public static Line of(String name, String color, Station upStation, Station downStation, Integer distance) {
        return new Line(name, color, upStation, downStation, distance);
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

    public void addSection(Section section) {
        sections.addSection(section);
        section.setLine(this);
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
