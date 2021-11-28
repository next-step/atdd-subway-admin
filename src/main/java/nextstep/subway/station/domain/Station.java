package nextstep.subway.station.domain;

import java.security.InvalidParameterException;
import java.util.Objects;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "deleted = false")
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    protected Station() {
    }

    public Station(String name) {
        validEmpty(name);

        this.name = name;
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    private void validEmpty(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new InvalidParameterException("빈 값을 입력 할 수 없습니다.");
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

        if (Objects.isNull(id)) {
            return false;
        }

        Station station = (Station) o;
        return id.equals(station.getId()) && name.equals(station.getName());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
