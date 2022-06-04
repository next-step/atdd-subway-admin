package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Column
    private Long distance;

    protected LineStation() {
    }

    private LineStation(Long id, Line line, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static LineStation of(Line line, Station upStation, Station downStation, Long distance) {
        return new LineStation(null, line, upStation, downStation, distance);
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public boolean isSame(LineStation newLineStation) {
        return this.equals(newLineStation);
    }

    public boolean isSmallerThanNewLineDistance(LineStation newLineStation) {
        return distance < newLineStation.getDistance();
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStation that = (LineStation) o;
        return Objects.equals(line, that.line) && Objects.equals(upStation, that.upStation) && Objects.equals(downStation, that.downStation) && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation, distance);
    }
}
