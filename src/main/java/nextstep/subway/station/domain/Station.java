package nextstep.subway.station.domain;

import nextstep.subway.common.domain.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StationName name;

    protected Station() {
    }

    private Station(StationName name) {
        this.name = name;
    }

    public static Station from(String name) {
        return new Station(StationName.from(name));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
