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
    @Column(nullable = false)
    private Integer distance;
    private LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color, Integer distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;

        this.lineStations.add(new LineStation(this, upStation, null, 0));
        this.lineStations.add(new LineStation(this, downStation, upStation, distance));
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

    public List<LineStation> getLineStations() {
        return lineStations.getLineStations();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void addSection(Station preStation, Station station, Integer duration) {
        LineStation preLineStation = getLineStations().stream().filter(it -> it.getStation() == preStation).findFirst()
                .orElseThrow(RuntimeException::new);
        if (preLineStation.getDuration() >= duration || distance <= duration) {
            throw new RuntimeException();
        }

        this.lineStations.add(new LineStation(this, station, preStation, duration));
    }
}
