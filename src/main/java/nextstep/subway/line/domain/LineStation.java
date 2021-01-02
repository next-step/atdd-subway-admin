package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    private int distanceForNextStation;

    public static final int DISTANCE_OF_LAST_STATION = 0;

    protected LineStation() {}

    public static LineStation createLineStation(Station station) {
        LineStation lineStation = new LineStation();
        lineStation.changeStation(station);
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

    public int getDistanceForNextStation() {
        return distanceForNextStation;
    }

    public void changeLine(Line line) {
        this.line = line;
    }

    public void changeStation(Station station) {
        this.station = station;
    }

    public void applyPreviousStationAndNextStationWithDistanceForNextStation(Station previousStation, Station nextStation, int distanceForNextStation) {
        this.previousStation = previousStation;
        this.nextStation = nextStation;
        if (nextStation != null && distanceForNextStation < 1) {
            throw new IllegalArgumentException("1 이상의 거리가 필요합니다");
        }
        this.distanceForNextStation = distanceForNextStation;
    }
}
