package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    private static final int DISTANCE_NONE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        return new Section(upStation, downStation, distance);
    }

    private static void validateDistance(int distance) {
        if (distance <= DISTANCE_NONE) {
            throw new IllegalArgumentException("거리 값은 " + DISTANCE_NONE + " 을 초과하는 값이어야 합니다.");
        }
    }

    public void toLine(Line line) {
        this.line = line;
        if (!line.contains(this)) {
            line.add(this);
        }
    }

    public void modifyDistance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public boolean dividable(Section target) {
        return this.distance > target.distance;
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
}
