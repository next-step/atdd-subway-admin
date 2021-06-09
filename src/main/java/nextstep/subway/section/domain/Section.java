package nextstep.subway.section.domain;

import java.util.List;
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

    public static final int UP_STATION_INDEX = 0;
    public static final int DOWN_STATION_INDEX = 1;
    public static final int VALID_STATIONS_SIZE = 2;

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

    public Section(final List<Station> stations, final int distance, final Line line) {
        final List<Station> validStations = validStations(stations);
        upStation = validStations.get(UP_STATION_INDEX);
        downStation = validStations.get(DOWN_STATION_INDEX);
        this.distance = validDistance(distance);
        this.line = line;
    }

    private List<Station> validStations(final List<Station> stations) {
        return Optional.ofNullable(stations)
            .filter(list -> list.size() == VALID_STATIONS_SIZE)
            .orElseThrow(IllegalArgumentException::new);
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
