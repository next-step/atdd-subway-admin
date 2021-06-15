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

    public Optional<LineStation> getPreviousStation() {
        return Optional.ofNullable(previousStation);
    }

    public Optional<Integer> getPreviousDistance() {
        return Optional.ofNullable(previousDistance);
    }

    public Optional<LineStation> getNextStation() {
        return Optional.ofNullable(nextStation);
    }

    public Optional<Integer> getNextDistance() {
        return Optional.ofNullable(nextDistance);
    }

    public void previous(final LineStation previousStation, final Integer previousDistance) {
        this.previousStation = validStation(previousStation);
        this.previousDistance = validDistance(previousDistance);
    }

    private LineStation validStation(final LineStation lineStation) {
        if (lineStation == null || this.equals(lineStation)) {
            return null;
        }

        return Optional.of(lineStation)
            .filter(ls -> ls.getStation().getName() != null && !ls.equals(this))
            .orElseThrow(IllegalArgumentException::new);
    }

    private Integer validDistance(final Integer distance) {
        if (distance == null || distance < 0) {
            return null;
        }

        return Optional.of(distance)
            .filter(d -> d > 0)
            .orElseThrow(IllegalArgumentException::new);
    }

    public void next(final LineStation lineStation, final Integer distance) {
        this.nextStation = validStation(lineStation);
        this.nextDistance = validDistance(distance);
    }

    public boolean isSameLine(final Line line) {
        return this.line.equals(line);
    }

    public void update(final LineStation upStation, final LineStation downStation, final int distance) {
        updatePrevious(upStation, downStation, distance);
        updateNext(upStation, downStation, distance);
    }

    private void updatePrevious(final LineStation upStation, final LineStation downStation, final int distance) {
        if (!this.equals(downStation)) {
            return;
        }

        validateDistance(distance, previousDistance);

        final Integer subtractedDistance = distance(previousDistance, distance);
        upStation.previous(previousStation, subtractedDistance);
        upStation.next(this, distance);
        if (previousStation != null) {
            previousStation.next(upStation, subtractedDistance);
        }
        this.previous(upStation, distance);
    }

    Integer distance(final Integer distance, final int newDistance) {
        if (distance == null) {
            return null;
        }

        return distance - newDistance;
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
        if (!this.equals(upStation)) {
            return;
        }

        validateDistance(distance, nextDistance);

        final Integer subtractedDistance = distance(nextDistance, distance);
        downStation.previous(this, distance);
        downStation.next(nextStation, subtractedDistance);
        if (nextStation != null) {
            nextStation.previous(downStation, subtractedDistance);
        }
        this.next(downStation, distance);
    }

    public void mergePrevAndNext() {
        final Integer distance = mergeDistance();
        Optional.ofNullable(previousStation)
            .ifPresent(s -> s.next(nextStation, distance));
        Optional.ofNullable(nextStation)
            .ifPresent(s -> s.previous(previousStation, distance));
    }

    private Integer mergeDistance() {
        if (previousDistance == null || nextDistance == null) {
            return null;
        }

        return previousDistance + nextDistance;
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
