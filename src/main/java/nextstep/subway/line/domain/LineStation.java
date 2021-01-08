package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(indexes = {@Index(columnList = "line_id, station_id", unique = true),
        @Index(columnList = "line_id, station_id, previous_station_id, next_station_id", unique = true)})
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_station_id")
    private Station previousStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_station_id")
    private Station nextStation;

    @Embedded
    private Distance distanceForNextStation;

    @Enumerated(EnumType.STRING)
    private PositionStatus status;

    protected LineStation() {}

    public LineStation(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    public static LineStation createLineStation(Station station, PositionStatus status) {
        LineStation lineStation = new LineStation();
        lineStation.changeStation(station);
        lineStation.changePositionStatus(status);
        return lineStation;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Station getPreviousStation() {
        return previousStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Distance getDistanceForNextStation() {
        return distanceForNextStation;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public void changeLine(Line line) {
        this.line = line;
    }

    public void changeStation(Station station) {
        this.station = station;
    }

    public void changePositionStatus(PositionStatus positionStatus) {
        this.status = positionStatus;
        if (isFirst()) {
            applyEmptyPreviousStation();
        }
        if (isLast()) {
            applyEmptyNextStation();
            applyDistanceForNextStation(new Distance(0));
        }
    }

    public void applyPreviousStation(Station station) {
        this.previousStation = station;
    }

    public void applyNextStation(LineStation lineStation) {
        if (!isLast()) {
            this.nextStation = lineStation.getStation();
            lineStation.applyPreviousStation(this.getStation());
        }
    }

    public void applyEmptyPreviousStation() {
        this.previousStation = null;
    }

    public void applyEmptyNextStation() {
        this.nextStation = null;
    }

    public void applyDistanceForNextStation(Distance distanceForNextStation) {
        if (!isLast()) {
            distanceForNextStation.checkZeroDistance();
        }
        this.distanceForNextStation = distanceForNextStation;
    }

    public void mergeDistanceForNextStation(Distance distance) {
        applyDistanceForNextStation(new Distance(distanceForNextStation.getDistance() + distance.getDistance()));
    }

    public void changeDistanceForNextStation(Distance newDistance) {
        if (newDistance.isDistanceGreaterThanEqual(distanceForNextStation)) {
            throw new IllegalArgumentException("기존 구간의 길이보다 작아야 합니다");
        }
        applyDistanceForNextStation(new Distance(distanceForNextStation.getDistance() - newDistance.getDistance()));
    }

    public boolean isFirst() {
        return status.isFirst();
    }

    public boolean isMiddle() {
        return status.isMiddle();
    }

    public boolean isLast() {
        return status.isLast();
    }

    public boolean isNew() {
        return id == null && getCreatedDate() == null;
    }

    public boolean isPersistence() {
        return id != null && getCreatedDate() != null;
    }
}
