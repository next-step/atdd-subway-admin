package nextstep.subway.domain.line;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "line")
public class Line {

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

    public Long getId() {
        return this.id;
    }

    public LineName getName() {
        return this.name;
    }

    public void changeName(LineName name) {
        this.name = name;
    }

    public LineColor getColor() {
        return this.color;
    }

    public void changeColor(LineColor color) {
        this.color = color;
    }

    public void delete() {
        this.sections.clear();
    }

    public void addSection(Station upStation, Station downStation, Integer distance) {
        Section section = new Section(this, upStation, downStation, new Distance(distance));
        this.sections.add(section);
    }

    public List<Station> getAllStations() {
        return this.sections.getAllStations();
    }

    public void removeSectionByStation(Station station) {
        this.sections.removeByStation(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (!Objects.equals(id, line.id)) return false;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name=" + name +
                ", color=" + color +
                '}';
    }
}
