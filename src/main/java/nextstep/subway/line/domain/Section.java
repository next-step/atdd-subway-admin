package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long upStation;
    private Long downStation;
    private int distance;

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station station;

    public Section() {

    }

    public Section(Long upStation, Long downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void changeSection(Long downStation, int addDistance) {
        this.downStation = downStation;
        this.distance = addDistance;
    }

    public void setLine(Line line) {
        this.line = line;
        //line.getSections().add(this);
    }

    public Long getUpStation() {
        return upStation;
    }

    public Long getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

}
