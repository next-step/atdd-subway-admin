package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private StationName stationName;

    protected Station() {}

    public Station(String name) {
        this.stationName = StationName.from(name);
    }

    public Station(StationName stationName) {
        this.stationName = stationName;
    }

    public static Station from(String name) {
        return new Station(StationName.from(name));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.stationName.getName();
    }
}
