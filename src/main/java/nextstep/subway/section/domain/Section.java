package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int distance;
    @ManyToOne
    private Station upStation;
    @ManyToOne
    private Station downStation;
    @ManyToOne
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void ofLine(Line line) {
        if (this.line != null) {
            this.line.getSections().remove(this);
        }
        this.line = line;
        line.getSections().add(this);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
