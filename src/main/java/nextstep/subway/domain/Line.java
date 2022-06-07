package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(null, name, color, upStation, downStation, distance);
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new Sections(new Section(this, upStation, downStation, distance));
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(this, upStation, downStation, distance);
    }

    public void deleteStation(Station station) {
        sections.delete(this, station);
    }

    public void change(String name, String color) {
        changeName(name);
        changeColor(color);
    }

    private void changeName(String name) {
        this.name = name;
    }

    private void changeColor(String color) {
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

    public List<Section> sections() {
        return sections.sections();
    }

    public List<Station> stations() {
        return sections.stations();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) &&
                Objects.equals(getName(), line.getName()) &&
                Objects.equals(getColor(), line.getColor()) &&
                Objects.equals(sections(), line.sections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), sections());
    }
}
