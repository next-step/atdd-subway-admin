package nextstep.subway.domain.station;

import nextstep.subway.domain.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "station")
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StationName name;

    public Station() {
    }

    public Station(StationName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public StationName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (!Objects.equals(id, station.id)) return false;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
