package nextstep.subway.domain;

import nextstep.subway.exception.StationException;

import javax.persistence.*;

import java.util.Objects;

import static nextstep.subway.exception.StationExceptionMessage.EMPTY_STATION_NAME;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    public Station() {
    }

    public Station(String name) {
        validateStation(name);
        this.name = name;
    }

    private void validateStation(String name) {
        if (name == null || name.isEmpty()) {
            throw new StationException(EMPTY_STATION_NAME.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) &&
                name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
