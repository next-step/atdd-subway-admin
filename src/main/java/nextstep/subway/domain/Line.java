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

    @Embedded
    private Sections sections = new Sections();

    private String name;
    private String color;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        addSection(upStation, downStation, distance);
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

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, distance);
        addSection(section);
    }

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }
}
