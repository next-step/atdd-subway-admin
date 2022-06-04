package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "upStation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<LineStation> upLineStations = new ArrayList<>();

    @OneToMany(mappedBy = "downStation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<LineStation> downLineStations = new ArrayList<>();

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public boolean isSame(Station upStation, Station newStation) {
        return upStation.equals(newStation);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<LineStation> getUpLineStations() {
        return upLineStations;
    }

    public List<LineStation> getDownLineStations() {
        return downLineStations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name) && Objects.equals(upLineStations, station.upLineStations) && Objects.equals(downLineStations, station.downLineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, upLineStations, downLineStations);
    }
}
