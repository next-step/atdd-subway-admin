package nextstep.subway.line.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationExistsAlreadyException;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany
    private final List<Station> stations = new LinkedList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Station> getStations() {
        return stations;
    }

    public void addStations(Station upStation, Station downStation) throws StationExistsAlreadyException {
        checkStationConflict(upStation);
        checkStationConflict(downStation);
        this.stations.add(upStation);
        this.stations.add(downStation);
    }

    private void checkStationConflict(Station station) throws StationExistsAlreadyException {
        if (this.stations.contains(station)) {
            throw new StationExistsAlreadyException(station);
        }
    }
}
