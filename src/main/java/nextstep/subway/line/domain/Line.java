package nextstep.subway.line.domain;

import java.util.List;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

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
    private Sections sections = new Sections();

    protected Line() {
    }

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

    Sections getSections() {
        return sections;
    }

    private void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sections.add(section);
    }
}
