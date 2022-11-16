package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preStationId")
    private Station preStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stationId")
    private Station station;

    private int distance;

    protected LineStation() {}

    public LineStation(Station station) {
        this.station = station;
    }

    public LineStation(Station preStation, Station station, int distance) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
    }

    public Station getStation() {
        return station;
    }

}
