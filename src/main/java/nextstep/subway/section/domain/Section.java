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
}
