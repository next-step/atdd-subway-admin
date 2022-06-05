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
    private Integer duration;

    public LineStation(Line line, Station station, Station preStation, Integer duration) {

        this.line = line;
        this.station = station;
        this.preStation = preStation;
        this.duration = duration;
    }

    protected LineStation() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineStation)) {
            return false;
        }
        LineStation that = (LineStation) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Station getPreStation() {
        return this.preStation;
    }
    public void updatePreStation(Station preStation) {
        this.preStation = preStation;
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
        if (this.station == o.preStation)
            return -1;
        return this.duration - o.duration;
    }

    public Station getStation() {
        return this.station;
    }

    public Integer getDuration() {
        return this.duration;
    }
}
