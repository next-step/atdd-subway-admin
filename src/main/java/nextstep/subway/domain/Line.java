package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Name name;
    @Embedded
    private Color color;
    @Embedded
    private Sections sections;

    @Embedded
    private Distance distance;

    protected Line() {
    }

    public Line(Name name, Color color, Station upStation, Station downStation, Distance distance) {
        this.name = name;
        this.color = color;
        this.sections = Sections.from(Section.of(this, upStation, downStation, distance));
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Distance getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void update(String name, String color) {
        this.name = new Name(name);
        this.color = new Color(color);
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public Sections getSections() {
        return sections;
    }
}
