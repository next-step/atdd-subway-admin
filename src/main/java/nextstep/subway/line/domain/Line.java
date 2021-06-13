package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() { }

    Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(Station upStation, Station downStation, String name, String color, int distance) {
        Line line = new Line(name, color);
        line.addSection(upStation, downStation, distance);
        return line;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<Station> orderedStations() {
        return sections.orderedStations();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, Distance.valueOf(distance));
        sections.addSection(section);
    }

    public void removeSectionBy(Station station) {
        sections.removeSectionBy(station);
    }

    Sections getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", color='" + color + '\'' +
            '}';
    }
}
