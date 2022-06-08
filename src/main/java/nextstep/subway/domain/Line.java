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
        this.sections = new Sections(this, distance, upStation, downStation);
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

    public List<Station> getAllSectionsSorted() {
        return this.sections.getAllDistinctStations();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(int distance, Station upStation, Station downStation) {
        this.sections.addSection(this, distance, upStation, downStation);
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
