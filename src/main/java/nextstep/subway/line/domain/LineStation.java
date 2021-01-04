package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static nextstep.subway.section.domain.Distance.*;

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

    private LineStation() {}

    public LineStation(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    public static LineStation createLineStation(Station station,
                                                Station previousStation,
                                                Station nextStation,
                                                Distance distanceForNextStation) {
        LineStation lineStation = new LineStation();
        lineStation.changeStation(station);
        lineStation.applyPreviousStationAndNextStation(previousStation, nextStation);
        lineStation.applyDistanceForNextStation(distanceForNextStation);
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

    public void changeLine(Line line) {
        this.line = line;
    }

    public void changeStation(Station station) {
        this.station = station;
    }

    public void applyPreviousStation(Station station) {
        this.previousStation = station;
    }

    public void applyNextStation(Station station) {
        this.nextStation = station;
    }

    public void applyPreviousStationAndNextStation(Station previousStation, Station nextStation) {
        applyPreviousStation(previousStation);
        applyNextStation(nextStation);
    }

    public void applyDistanceForNextStation(Distance distanceForNextStation) {
        checkDistance(distanceForNextStation);
        this.distanceForNextStation = distanceForNextStation;
    }

    public void changeDistanceForNextStation(Distance newDistance) {
        Distance changedDistance = new Distance(distanceForNextStation.getDistance() - newDistance.getDistance());
        applyDistanceForNextStation(changedDistance);
    }

    private void checkDistance(Distance distance) {
        if (nextStation != null && distance.getDistance() == MIN_DISTANCE) {
            throw new IllegalArgumentException(MIN_DISTANCE + "보다 큰 거리만 허용됩니다");
        } else if (isLast() && distance.getDistance() > MIN_DISTANCE) {
            throw new IllegalArgumentException("거리가 " + MIN_DISTANCE + "만 허용됩니다");
        }
    }

    public boolean isFirst() {
        return previousStation == null;
    }

    public boolean isLast() {
        return nextStation == null;
    }

    public boolean isNew() {
        return id == null && getCreatedDate() == null;
    }

    public boolean isPersistence() {
        return id != null && getCreatedDate() != null;
    }
}
