package nextstep.subway.station.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StationName name;

    protected Station() {}

    private Station(String name) {
        this.name = StationName.from(name);
    }

    private Station(Long id) {
        this.id = id;
    }

    private Station(Long id, StationName name) {
        this.id = id;
        this.name = name;
    }

    public static Station from(Long stationId) {
        return new Station(stationId);
    }

    public static Station from(String name) {
        return new Station(name);
    }

    public static Station from(Long id, String name) {
        return new Station(id, StationName.from(name));
    }

    public Long getId() {
        return id;
    }

    public StationName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Station station = (Station)o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
