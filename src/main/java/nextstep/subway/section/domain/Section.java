package nextstep.subway.section.domain;

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
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Long distance) {
        validateUpDownStation(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean containDownStation(Station station) {
        return station.equals(downStation);
    }

    public boolean containUpStation(Station station) {
        return station.equals(upStation);
    }

    public boolean isPostSection(Section preSection) {
        return upStation != null && upStation.equals(preSection.getDownStation());
    }

    public void changeLine(Line line) {
        this.line = line;
    }

    public void changeDownStation(Station downStation, Long distance) {
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean checkDistance(Long distance) {
        return this.distance > distance;
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

    public Long getDistance() {
        return distance;
    }

}
