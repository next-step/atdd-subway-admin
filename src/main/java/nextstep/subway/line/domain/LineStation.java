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
@Table(indexes = {@Index(columnList = "line_id, station_id, previous_station_id, next_station_id", unique = true)})
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

    protected LineStation() {}

    public static LineStation createLineStation(Station station,
                                                Station previousStation,
                                                Station nextStation,
                                                int distanceForNextStation) {
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

    public void applyPreviousStationAndNextStation(Station previousStation, Station nextStation) {
        this.previousStation = previousStation;
        this.nextStation = nextStation;
    }

    public void applyDistanceForNextStation(int distanceForNextStation) {
        checkDistance(distanceForNextStation);
        this.distanceForNextStation = new Distance(distanceForNextStation);
    }

    private void checkDistance(int distance) {
        if (nextStation != null && distance == MIN_DISTANCE) {
            throw new IllegalArgumentException(MIN_DISTANCE + "보다 큰 거리만 허용됩니다");
        } else if (nextStation == null && distance > MIN_DISTANCE) {
            throw new IllegalArgumentException("거리가 " + MIN_DISTANCE + "만 허용됩니다");
        }
    }
}
