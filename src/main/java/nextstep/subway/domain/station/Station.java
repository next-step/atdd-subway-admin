package nextstep.subway.domain.station;

import nextstep.subway.domain.LineStation.LineStations;
import nextstep.subway.domain.common.BaseEntity;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StationName name;

    @Embedded
    private LineStations lineStations = LineStations.create();

    protected Station() {
    }

    private Station(String name) {
        this.name = StationName.of(name);
    }

    public static Station create(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }
}
