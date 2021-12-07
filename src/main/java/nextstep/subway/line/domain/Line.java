package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.domain.Name;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    @Column
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = new Name(name);
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = new Name(name);
        this.color = color;
        this.sections.addSection(this, upStation, downStation, distance);
    }

    public void update(Line line) {
        this.name = new Name(line.getName());
        this.color = line.getColor();
    }

    public Line addSection(Station upStation, Station downStation, int distance) {
        this.sections.addSection(this, upStation, downStation, distance);
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name.printName();
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return this.sections;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name=" + name +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }
}
