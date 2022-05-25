package nextstep.subway.station.domain;

import java.util.Objects;
import javax.persistence.*;
import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private StationName stationName;

    protected Station() {}

    public Station(String name) {
        this.stationName = StationName.from(name);
    }

    public Station(StationName stationName) {
        this.stationName = stationName;
    }

    public Station(Long id, StationName stationName) {
        this.id = id;
        this.stationName = stationName;
    }

    public static Station from(String name) {
        return new Station(StationName.from(name));
    }

    public static Station of(Long id, String name) {
        return new Station(id, StationName.from(name));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.stationName.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(getId(), station.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
