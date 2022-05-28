package nextstep.subway.domain.section;

import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.common.Distance;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    public static Section create(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.of(distance);
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
