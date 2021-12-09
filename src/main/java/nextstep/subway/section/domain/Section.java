package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Station upStation;
    @ManyToOne
    private Station downStation;
    private int distance;
    @ManyToOne
    private Line line;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
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

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public boolean isUpStation(Station station) {
        return station.isSameStation(this.upStation);
    }

    public boolean isDownStation(Station station) {
        return station.isSameStation(this.downStation);
    }

    public void updateDownStation(Station newStation, Section newSection) {
        downStation = newStation;
        distance = distance - newSection.distance;
    }

    public void updateUpStation(Station newStation, Section newSection) {
        upStation = newStation;
        distance = distance - newSection.distance;
    }
}
