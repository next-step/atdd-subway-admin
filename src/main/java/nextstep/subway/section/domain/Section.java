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
    @JoinColumn(name = "fk_line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "fk_up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "fk_down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {

    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section newInstance(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void toLine(Line line) {
        this.line = line;
    }
}
