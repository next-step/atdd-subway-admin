package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {

    public static final String ERROR_MESSAGE_DUPLICATE_STATION = "동일한 역으로 구간을 생성할 수 없습니다.";
    public static final String ERROR_MESSAGE_DISTANCE_BOUND = "역간 거리는 0 이하가 될 수 없습니다.";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validateDuplicateStation(upStation, downStation);
        validateDistanceBound(distance);

        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    private void validateDuplicateStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_STATION);
        }
    }

    private void validateDistanceBound(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DISTANCE_BOUND);
        }
    }

    public static Section of(Long id, Line line, Station upStation, Station downStation, int distance) {
        return new Section(id, line, upStation, downStation, distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(null, line, upStation, downStation, distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
