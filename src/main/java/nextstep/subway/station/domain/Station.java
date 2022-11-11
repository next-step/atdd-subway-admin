package nextstep.subway.station.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.message.ExceptionMessage;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    protected Station() {
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station from(String name) {
        validate(name);
        return new Station(name);
    }

    private static void validate(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.REQUIRED);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
