package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    private Integer distance;
    @OneToMany(targetEntity = LineStation.class, mappedBy = "station", fetch = FetchType.LAZY)
    private List<LineStation> lineStations;

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, 0);
    }

    public Line(String name, String color, Integer distance) {
        this(null, name, color, distance);
    }

    public Line(Long id, String name, String color, Integer distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
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

    public Integer getDistance() {
        return distance;
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public void setLineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }
}
