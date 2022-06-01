package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
        return station.isSame(downStation);
    }

    public boolean containUpStation(Station station) {
        return station.isSame(upStation);
    }

    public boolean isPostSection(Section preSection) {
        return upStation != null && upStation.isSame(preSection.getDownStation());
    }

    public void changeLine(Line line) {
        this.line = line;
    }

    public void changeSection(Section changeSection) {
        validateDistance(changeSection);

        if (this.containUpStation(changeSection.getUpStation())) {
            this.upStation = changeSection.getDownStation();
        }

        if (this.containDownStation(changeSection.getDownStation())) {
            this.downStation = changeSection.getUpStation();
        }

        this.distance -= changeSection.getDistance();
    }

    private void validateUpDownStation(Station upStation, Station downStation) {
        if (upStation == null && downStation == null) {
            throw new IllegalArgumentException("상행종점역, 하행종점역이 존재하지 않습니다.");
        }

        if (upStation != null && upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행종점역과 하행종점역은 같을 수가 없습니다.");
        }
    }

    private void validateDistance(Section changeSection) {
        if (!this.checkDistance(changeSection.getDistance())) {
            throw new IllegalArgumentException("구간의 거리는 기존 구간보다 작아야 합니다.");
        }
    }

    private boolean checkDistance(Long distance) {
        return this.distance > distance;
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
