package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.lineStation.domain.LineStation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "station", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public void addLineStation(LineStation lineStation) {
        this.lineStations.add(lineStation);
        lineStation.setStation(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<LineStation> getLineStations() {
        return this.lineStations;
    }
}
