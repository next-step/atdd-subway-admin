package nextstep.subway.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "line_station")
public class LineStation implements Comparable<LineStation> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station station;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Station preStation;
    private Integer distance;

    private LineStation(Line line, Station station, Station preStation, Integer distance) {
        this.line = line;
        this.station = station;
        this.preStation = preStation;
        this.distance = distance;
    }

    protected LineStation() {}

    public static LineStation ascEndSection(Line line, Station station) {
        return new LineStation(line, station, null, 0);
    }

    public static LineStation section(Line line, Station station, Station preStation, Integer distance) {
        return new LineStation(line, station, preStation, distance);
    }

    public Station getPreStation() {
        return this.preStation;
    }

    public void updatePreStation(Station preStation) {
        this.preStation = preStation;
    }

    public void updateDuration(Integer duration) {
        this.distance = duration;
    }

    public Long getStationId() {
        return this.station.getId();
    }

    public String getName() {
        return this.station.getName();
    }

    public LocalDateTime getCreatedDate() {
        return this.station.getCreatedDate();
    }

    public LocalDateTime getModifiedDate() {
        return this.station.getModifiedDate();
    }

    @Override
    public int compareTo(LineStation o) {
        if (Objects.equals(this.station, o.preStation))
            return -1;
        return this.distance - o.distance;
    }

    public Station getStation() {
        return this.station;
    }

    public Integer getDistance() {
        return this.distance;
    }
}
