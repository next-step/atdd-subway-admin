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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
    private Long preStationId;
    private Integer distance;

    protected LineStation() {
    }

    public LineStation(Station station, Long preStationId, Integer distance) {
        this.station = station;
        this.preStationId = preStationId;
        this.distance = distance;
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

    public void updateFirstNode(Long preStationId) {
        this.station = new Station(preStationId);
    }

    public void updatePreStationId(Long id) {
        this.preStationId = id;
    }
}
