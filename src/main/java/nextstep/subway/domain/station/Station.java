package nextstep.subway.domain.station;

import nextstep.subway.domain.BaseEntity;

import javax.persistence.*;

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
}
