package nextstep.subway.domain.station;

import nextstep.subway.domain.LineStation.LineStations;
import nextstep.subway.domain.common.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StationName name;

    @Embedded
    private LineStations lineStations = LineStations.create();

    protected Station() {
    }

    private Station(String name) {
        this.name = StationName.of(name);
    }

    public static Station create(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
