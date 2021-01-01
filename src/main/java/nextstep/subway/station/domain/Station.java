package nextstep.subway.station.domain;

import java.util.Objects;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import org.apache.commons.lang3.StringUtils;

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
        validation(name);
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private void validation(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Station의 name은 필수 값 입니다.");
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
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
