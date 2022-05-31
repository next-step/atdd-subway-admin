package nextstep.subway.line_station.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    protected LineStation() {
    }

    public LineStation(Station upStation, Station downStation, Long distance) {
        validateUpDownStation(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public LineStation upStationEndPoint(LineStation lineStation) {
        return new LineStation(null, lineStation.upStation, lineStation.distance);
    }

    public boolean isDownStation(LineStation preLineStation) {
        return upStation != null && upStation.getId().equals(preLineStation.getDownStation().getId());
    }

    public boolean isFirstStation() {
        return this.upStation == null;
    }

    public void changeLine(Line line) {
        this.line = line;
    }

    private void validateUpDownStation(Station upStation, Station downStation) {
        if (upStation == null && downStation == null) {
            throw new IllegalArgumentException("상행종점역, 하행종점역이 존재하지 않습니다.");
        }

        if (upStation != null && upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행종점역과 하행종점역은 같을 수가 없습니다.");
        }
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

}
