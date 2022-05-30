package nextstep.subway.station.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StationName name;

    protected Station() {}

    public Station(Long id, StationName name) {
        this.id = id;
        this.name = name;
    }

    public Station(Long id, String name) {
        this(id, StationName.from(name));
    }

    public Station(String name) {
        this(0L, StationName.from(name));
    }

    public StationResponse toStationResponse() {
        return StationResponse.from(this);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
