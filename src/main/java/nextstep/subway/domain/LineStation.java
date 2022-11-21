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
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "current_station_id", foreignKey = @ForeignKey(name = "fk_line_current_station"))
    private Station currentStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "next_station_id", foreignKey = @ForeignKey(name = "fk_line_next_station"))
    private Station nextStation;
    private Integer distance;

    protected LineStation() {
    }

    public LineStation(Line line, Station currentStation, Station nextStation, Integer distance) {
        this.line = line;
        this.currentStation = currentStation;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public LineStation changeBetweenSection(LineStation lineStation) {
        LineStation newLineStation = new LineStation(line, lineStation.nextStation, nextStation, distance - lineStation.distance);
        this.nextStation = lineStation.nextStation;
        this.distance = lineStation.distance;
        return newLineStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineStation)) return false;
        LineStation that = (LineStation) o;
        return Objects.equals(line, that.line) && Objects.equals(currentStation, that.currentStation) && Objects.equals(nextStation, that.nextStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, currentStation, nextStation);
    }
}
