package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.exception.IllegalDistanceError;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Station upStation;

    @OneToOne
    private Station downStation;

    private int distance;

    @ManyToOne
    private Line line;

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section() {

    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalDistanceError();
        }
        this.distance = distance;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }
}
