package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
    private List<Station> stations = new ArrayList<>();

    public Line() {
    }

    public Line(String name) {
        this.name = name;
    }

    public void addStation(Station station) {
        this.stations.add(station);
        station.setLine(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Station> getStations() {
        return stations;
    }
}
