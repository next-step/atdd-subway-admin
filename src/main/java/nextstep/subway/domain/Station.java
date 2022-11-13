package nextstep.subway.domain;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Objects;

@Entity
@DynamicInsert
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @ColumnDefault("false")
    private boolean isAscentEndPoint;
    @ColumnDefault("false")
    private boolean isDeAscentEndPoint;

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void changeAscentEndPoint(final Boolean ascentEndPoint) {
        isAscentEndPoint = ascentEndPoint;
    }

    public void changeDeAscentEndPoint(final Boolean deAscentEndPoint) {
        isDeAscentEndPoint = deAscentEndPoint;
    }

    public Boolean isAscentEndPoint() {
        return isAscentEndPoint;
    }

    public Boolean isDeAscentEndPoint() {
        return isDeAscentEndPoint;
    }
}
