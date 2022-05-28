package nextstep.subway.domain.station;

import nextstep.subway.domain.LineStation.LineStation;
import nextstep.subway.domain.common.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StationName name;

    @OneToMany(
            mappedBy = "station",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<LineStation> lineStations = new ArrayList<>();

    protected Station() {
    }

    public Station(String name) {
        this.name = StationName.of(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }
}
