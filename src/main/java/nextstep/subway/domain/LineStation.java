package nextstep.subway.domain;

import javax.persistence.Column;
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
    private Station downLineStation;

    @Column(name = "pre_station_id")
    private Long upLineStationId;

    private Integer distance;

    protected LineStation() {
    }

    public LineStation(Station downLineStation, Long upLineStationId, Integer distance) {
        this.downLineStation = downLineStation;
        this.upLineStationId = upLineStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getDownLineStation() {
        return downLineStation;
    }

    public Long getUpLineStationId() {
        return upLineStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void updateFirstNode(Long stationId) {
        this.downLineStation = new Station(stationId);
    }

    public void updatePreStationAndDistance(Long id, Integer distance) {
        this.upLineStationId = id;
        this.distance = this.distance - distance;
    }
}
