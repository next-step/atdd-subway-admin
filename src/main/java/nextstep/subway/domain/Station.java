package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Station() {}

    public Station(String name) {
        validation(name);
        this.name = name;
    }

    private void validation(String name) {
        if (name == null || name.isEmpty()) {
            new IllegalArgumentException(ErrorCode.NO_EMPTY_STATION_NAME_EXCEPTION.getErrorMessage());
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
        if (o == null) return false;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return id != null ? id.equals(station.getId()) : station.getId() == null;
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }
}
