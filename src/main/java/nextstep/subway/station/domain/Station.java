package nextstep.subway.station.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StationName name;

    protected Station() {}

    private Station(String name) {
        this.name = StationName.from(name);
    }

    private Station(Long id) {
        this.id = id;
    }

    public static Station from(Long stationId) {
        return new Station(stationId);
    }

    public static Station from(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public StationName getName() {
        return name;
    }
}
