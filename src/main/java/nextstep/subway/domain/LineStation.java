package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // @Column(nullable = false)
    // private Long stationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
    private Long preStationId;
    private Integer distance;

    protected LineStation() {
    }

    public static LineStation of(Long preStationId, Long stationId, Integer distance) {
        LineStation lineStation = new LineStation();
        lineStation.station = new Station(stationId);
        lineStation.preStationId = preStationId;
        lineStation.distance = distance;
        return lineStation;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
