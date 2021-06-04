package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    private final String CAN_NOT_STATION_EQUALS = "동일한 역을 등록할 수 없습니다.";
    private final String NOT_ENOUGH_DISTANCE = "충분하지 않은 구간 크기입니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        stationEqualsValidate(upStation, downStation);
        distanceValidate(distance);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void stationEqualsValidate(Station upStation, Station downStation)  {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(CAN_NOT_STATION_EQUALS);
        }
    }

    private void distanceValidate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException(NOT_ENOUGH_DISTANCE);
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
