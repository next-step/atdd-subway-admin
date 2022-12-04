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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = true)
    private Station upStation;

    private Integer distance;

    protected Section() {
    }

    public Section(Station downStation, Station upStation, Integer distance) {
        this.downStation = downStation;
        this.upStation = upStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public void updateWhenDownStationExists(Station station, Integer newDistance) {
        minusDistance(newDistance);
        this.downStation = station;
    }

    public void updateWhenUpStationExists(Station station, Integer newDistance) {
        minusDistance(newDistance);
        this.upStation = station;
    }

    public void updateUpStationAndMergeDistance(Station upStation, Integer distance) {
        this.upStation = upStation;
        if (distance != null) {
            this.distance += distance;
        }
    }

    private void validateDistance(Integer newDistance, Integer nowDistance) {
        if (nowDistance <= newDistance) {
            throw new ShortDistanceException(nowDistance, newDistance);
        }
    }

    private void minusDistance(Integer newDistance) {
        if (this.distance != null) {
            validateDistance(newDistance, this.distance);
            this.distance = this.distance - newDistance;
        }
    }

}
