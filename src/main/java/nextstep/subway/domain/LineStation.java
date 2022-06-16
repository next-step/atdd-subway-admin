package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected LineStation() {
    }

    private LineStation(Long id, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static LineStation of(Station upStation, Station downStation, Distance distance) {
        return new LineStation(null, upStation, downStation, distance);
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public void updateUpStation(LineStation lineStation) {
        distance.updateDistance(lineStation.getDistance());
        upStation = lineStation.getDownStation();
    }

    public void downUpStation(LineStation lineStation) {
        distance.updateDistance(lineStation.getDistance());
        downStation = lineStation.getUpStation();
    }

    public void deleteLine() {
        this.line = null;
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

    public Distance getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStation that = (LineStation) o;
        return Objects.equals(line, that.line) && Objects.equals(upStation, that.upStation) && Objects.equals(downStation, that.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }

    @Override
    public String toString() {
        return "LineStation{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
