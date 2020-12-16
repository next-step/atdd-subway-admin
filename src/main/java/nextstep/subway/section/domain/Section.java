package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.exceptions.InvalidSectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    private static final Long MIN_DISTANCE = 0L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Long distance;

    protected Section() {
    }

    public Section(final Line line, final Station upStation, final Station downStation, final Long distance) {
        validate(upStation, downStation, distance);
        validateDistance(distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(final Station upStation, final Station downStation, final Long distance) {
        validateStations(upStation, downStation);
        validateDistance(distance);
    }

    private void validateStations(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new InvalidSectionException("상행역과 하행역은 같은 역일 수 없습니다.");
        }
    }

    private void validateDistance(final Long distance) {
        if (distance.equals(MIN_DISTANCE)) {
            throw new InvalidSectionException("거리가 0인 구간은 생성할 수 없습니다.");
        }
    }
}
