package nextstep.subway.linestation.domain;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "prev_station_id")
    private LineStation previousStation;

    private Integer previousDistance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "next_station_id")
    private LineStation nextStation;

    private Integer nextDistance;

    protected LineStation() {
    }

    public LineStation(final Line line, final Station station) {
        this.line = line;
        this.station = station;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

    public LineStation getPreviousStation() {
        return previousStation;
    }

    public Integer getPreviousDistance() {
        return previousDistance;
    }

    public LineStation getNextStation() {
        return nextStation;
    }

    public Integer getNextDistance() {
        return nextDistance;
    }

    public void previous(final LineStation previousStation, final Integer previousDistance) {
        this.previousStation = validStation(previousStation);
        this.previousDistance = validDistance(previousDistance);
    }

    private LineStation validStation(final LineStation lineStation) {
        return Optional.ofNullable(lineStation)
            .filter(ls -> ls.getStation().getName() != null && !ls.equals(this))
            .orElseThrow(IllegalArgumentException::new);
    }

    private int validDistance(final Integer distance) {
        return Optional.ofNullable(distance)
            .filter(d -> d > 0)
            .orElseThrow(IllegalArgumentException::new);
    }

    public void next(final LineStation nextStation, final Integer nextDistance) {
        this.nextStation = validStation(nextStation);
        this.nextDistance = validDistance(nextDistance);
    }

    public boolean isSameLine(final Line line) {
        return this.line.equals(line);
    }

    public void update(final LineStation upStation, final LineStation downStation, final int distance) {
        updatePrevious(upStation, downStation, distance);
        updateNext(upStation, downStation, distance);
    }

    private void updatePrevious(final LineStation upStation, final LineStation downStation, final int distance) {
        if (upStation.equals(previousStation)) {
            validateDistance(distance, previousDistance);
            previousStation = downStation;
            previousDistance -= distance;

            return;
        }

        if (this.equals(downStation)) {
            validateDistance(distance, previousDistance);
            previousStation = upStation;
            previousDistance = distance;
        }
    }

    private void validateDistance(final int distance, final Integer targetDistance) {
        if (targetDistance == null) {
            return;
        }

        if (distance >= targetDistance) {
            throw new IllegalArgumentException();
        }
    }

    private void updateNext(final LineStation upStation, final LineStation downStation, final int distance) {
        if (downStation.equals(nextStation)) {
            validateDistance(distance, nextDistance);
            nextStation = upStation;
            nextDistance -= distance;

            return;
        }

        if (this.equals(upStation)) {
            validateDistance(distance, nextDistance);
            nextStation = downStation;
            nextDistance = distance;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final LineStation that = (LineStation)o;
        return line.equals(that.line) && station.equals(that.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, station);
    }
}
