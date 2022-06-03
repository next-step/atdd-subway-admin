package nextstep.subway.station.domain;

import nextstep.subway.common.domain.BaseEntity;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StationName name;

    public Station() {
    }

    private Station(String name) {
        this.name = StationName.from(name);
    }

    public static Station from(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof  Station)) {
            return false;
        }
        return ((Station)obj).getId() == id && ((Station)obj).getName() == this.getName();
    }
}
