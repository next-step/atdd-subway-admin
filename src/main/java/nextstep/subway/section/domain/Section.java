package nextstep.subway.section.domain;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final int distance, final Line line) {
        this.upStation = validStation(upStation);
        this.downStation = validStation(downStation);
        this.distance = validDistance(distance);
        this.line = line;
    }

    private Station validStation(final Station station) {
        return Optional.ofNullable(station)
            .orElseThrow(IllegalArgumentException::new);
    }

    private int validDistance(final int distance) {
        return Optional.of(distance)
            .filter(integer -> integer > 0)
            .orElseThrow(IllegalArgumentException::new);
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

    public int getDistance() {
        return distance;
    }
}
