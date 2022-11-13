package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
    private Sections sections = Sections.createEmpty();

    protected Line() {
    }

    private Line(Long id, LineName name, LineColor color, Section section) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.addSection(section);
    }

    public static Line of(String name, String color, Section section) {
        return of(null, name, color, section);
    }

    public static Line of(Long id, String name, String color, Section section) {
        return new Line(id, LineName.from(name), LineColor.from(color), section);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }

    public void update(String name, String color) {
        this.name = LineName.from(name);
        this.color = LineColor.from(color);
    }

    public void addSection(Section section) {
        section.addTo(this);
        this.sections.add(section);
    }

    public List<Station> getStationsInOrder() {
        return this.sections.getStationsInOrder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && name.equals(line.name) && color.equals(line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
