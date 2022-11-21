package nextstep.subway.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "line_station")
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "pre_station_id", foreignKey = @ForeignKey(name = "fk_line_next_station"))
    private Station preStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_line_current_station"))
    private Station station;
    private Integer distance;

    protected LineStation() {
    }

    public LineStation(Station preStation, Station station, Integer distance) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getPreStation() {
        return preStation;
    }

    public Station getStation() {
        return station;
    }

    public Integer getDistance() {
        return distance;
    }

    public void updateLineStation(LineStation newLineStation) {
        validateDistance(newLineStation);
        this.preStation = newLineStation.station;
        this.distance = this.distance - newLineStation.distance;
    }

    public void updatePreLineStation(LineStation newLineStation) {
        if (this.preStation != null) {
            validateDistance(newLineStation);
            this.distance = this.distance - newLineStation.distance;
        }
        this.station = newLineStation.preStation;
    }

    private void validateDistance(LineStation newLineStation) {
        if (this.distance - newLineStation.distance <= 0) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 새로운 역의 구간 길이가 깁니다!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineStation)) return false;
        LineStation that = (LineStation) o;
        return Objects.equals(station, that.station) && Objects.equals(preStation, that.preStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, preStation);
    }
}
