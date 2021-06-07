package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateSameStations(upStation, downStation);
        validateDistance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public String getUpStationName() {
        return this.upStation.getName();
    }

    public String getDownStationName() {
        return this.downStation.getName();
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return this.line;
    }

    public void registerLine(Line line) {
        this.line = line;
    }

    public List<Station> getStations() {
        return new ArrayList<>(Arrays.asList(this.upStation, this.downStation));
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 거리는 1 이상이어야 합니다.");
        }
    }

    private void validateSameStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행, 하행역은 동일한 역일 수 없습니다.");
        }
    }
}
