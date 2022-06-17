package nextstep.subway.line.domain;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.station.domain.Station;

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

    protected Line() {
    }

    public Line(String name, String color) {
        this(null, new LineName(name), new LineColor(color), new Sections());
    }

    public Line(Long id, LineName name, LineColor color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        Section section = Section.of(upStation, downStation, distance);

        Line line = new Line(name, color);
        line.addSection(section);

        return line;
    }

    public void addSection(Section section) {
        section.toLine(this);
        sections.add(section);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.value();
    }

    public String getColor() {
        return color.value();
    }

    public List<Station> getStations() {
        return sections.getAllStation();
    }

    public void update(String name, String color) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
    }
}
