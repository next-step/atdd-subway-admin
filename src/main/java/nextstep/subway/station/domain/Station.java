package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Column(unique = true)
    private String name;

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public static Station of(String name) {

        return new Station(name);
    }

    public String getName() {
        return name;
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
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                '}';
    }
}
