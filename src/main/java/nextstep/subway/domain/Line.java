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
}
