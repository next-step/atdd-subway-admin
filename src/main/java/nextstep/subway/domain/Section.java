package nextstep.subway.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "line_station")
public class Section implements Comparable<Section> {
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

    private Section(Line line, Station station, Station preStation, Integer distance) {
        this.line = line;
        this.station = station;
        this.preStation = preStation;
        this.distance = distance;
    }

    protected Section() {}

    public static Section ascendEndPoint(Line line, Station station) {
        return new Section(line, station, null, 0);
    }

    public static Section of(Line line, Station station, Station preStation, Integer distance) {
        return new Section(line, station, preStation, distance);
    }

    public Long getId() {
        return id;
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
    public int compareTo(Section o) {
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
