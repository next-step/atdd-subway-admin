package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;
import org.springframework.util.Assert;

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

    protected Line() {
    }

    private Line(Name name, Color color, Sections sections) {
        setName(name);
        setColor(color);
        setSections(sections);
    }

    public static Line of(Name name, Color color, Sections sections) {
        return new Line(name, color, sections);
    }

    public void update(Name name, Color color) {
        setName(name);
        setColor(color);
    }

    public Long id() {
        return id;
    }

    public Name name() {
        return name;
    }

    public Color color() {
        return color;
    }

    public List<Station> stations() {
        return sections.stations();
    }

    public void addSection(Section section) {
        Assert.notNull(section, "added section must not be null");
        section.setLine(this);
        sections.addSection(section);
    }

    public void deleteStation(Station station) {
        sections.deleteStation(station);
    }

    private void setName(Name name) {
        Assert.notNull(name, "'name' must not be null");
        this.name = name;
    }

    private void setColor(Color color) {
        Assert.notNull(color, "'color' must not be null");
        this.color = color;
    }

    private void setSections(Sections sections) {
        Assert.notNull(sections, "'sections' must not be null");
        sections.setLine(this);
        this.sections = sections;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name);
    }

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", name=" + name +
            ", color=" + color +
            ", sections=" + sections +
            '}';
    }
}
