package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.IllegalDistanceException;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Entity
@Embeddable
public class Section extends BaseEntity {

    private int distance;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    protected Section() {
    }

    public Section(int distance, Station upStation, Station downStation) {
        validate(distance, upStation, downStation);
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validate(int distance, Station upStation, Station downStation) {
        validateDistance(distance);
        if (Objects.isNull(upStation)) {
            throw new IllegalArgumentException("상행역은 빈값일 수 없습니다.");
        }
        if (Objects.isNull(downStation)) {
            throw new IllegalArgumentException("하행역은 빈값일 수 없습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalDistanceException();
        }
    }

    public static Section of(int distance, Station upStation, Station downStation) {
        return new Section(distance, upStation, downStation);
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
