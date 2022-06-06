package nextstep.subway.domain;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@DynamicUpdate
@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;
    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Integer distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(Section.ascendEndPoint(this, upStation), Section.of(this, downStation, upStation, distance));
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

    public List<Section> getAllSections() {
        return this.sections.getAll();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station preStation, Station station, Integer distance) {
        this.sections.addSection(this, preStation, station, distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return getId().equals(line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
