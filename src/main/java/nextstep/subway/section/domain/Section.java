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

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @Column
    private int distance;

    protected Section(){

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
}
