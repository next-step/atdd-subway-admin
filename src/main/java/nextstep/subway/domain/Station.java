package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @OneToMany(targetEntity = LineStation.class, mappedBy = "line", fetch = FetchType.LAZY)
    private List<LineStation> lineStations;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }
}
