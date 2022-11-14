package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @OneToMany(mappedBy = "line")
    private List<Station> stations;
    private Integer distance;

    protected Line() {
        this.stations = new ArrayList<>();
    }

    public Line(String name, String color, Integer distance) {
        this(name, color, new ArrayList<>(), distance);
    }

    public Line(String name, String color, List<Station> stations, Integer distance) {
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.distance = distance;
    }

    public void addStation(Station station) {
        station.toLine(this);
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getName(), line.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
