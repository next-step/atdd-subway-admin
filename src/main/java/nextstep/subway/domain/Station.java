package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.*;
import nextstep.subway.message.ErrorMessage;
import org.springframework.util.ObjectUtils;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    protected Station() {
    }

    public static Station createStation(String name) {
        return new Station(name);
    }

    private Station(String name) {
        valid(name);
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private void valid(String name) {
        if (ObjectUtils.isEmpty(name)) {
            throw new IllegalArgumentException(ErrorMessage.STATION_NAME_IS_ESSENTIAL.toMessage());
        }
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
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
