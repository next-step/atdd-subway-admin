package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Entity
@Embeddable
public class Section extends BaseEntity {

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    protected Section() {
    }

    public Section(int distance, Station upStation, Station downStation) {
        validate(upStation, downStation);
        this.distance = Distance.of(distance);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validate(Station upStation, Station downStation) {
        if (Objects.isNull(upStation)) {
            throw new IllegalArgumentException("상행역은 빈값일 수 없습니다.");
        }
        if (Objects.isNull(downStation)) {
            throw new IllegalArgumentException("하행역은 빈값일 수 없습니다.");
        }
    }

    public static Section of(int distance, Station upStation, Station downStation) {
        return new Section(distance, upStation, downStation);
    }

    public static Section of(int distance, String upStation, String downStation) {
        return new Section(distance, Station.of(upStation), Station.of(downStation));
    }

    public int getDistance() {
        return distance.getDistance();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    @Override
    public String toString() {
        return "Section{" +
                "distance=" + distance +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                '}';
    }
}
