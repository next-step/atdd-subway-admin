package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line extends BaseEntity {
    @Embedded
    private final Sections sections = new Sections();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    protected Line() {
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections.addSection(
            new Section(this, upStation, downStation, distance)
        );
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this(null, name, color, upStation, downStation, distance);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null, null, null);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void saveSection(Section section) {
        this.sections.addSection(section);
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

    public List<Station> getStations() {
        return sections.getStations();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Line line = (Line)o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
