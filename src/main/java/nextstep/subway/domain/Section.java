package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.exception.ShortDistanceException;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    private Long upStationId;

    private Integer distance;

    protected Section() {
    }

    public Section(Station downStation, Long upStationId, Integer distance) {
        this.downStation = downStation;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void updateFirstNode(Long stationId) {
        this.downStation = new Station(stationId);
    }

    public void updatePreStationAndDistance(Long id, Integer newDistance) {
        validateDistance(newDistance, this.distance);
        this.upStationId = id;
        this.distance = this.distance - newDistance;
    }

    private void validateDistance(Integer newDistance, Integer nowDistance) {
        if (nowDistance <= newDistance) {
            throw new ShortDistanceException(nowDistance, newDistance);
        }
    }
}
