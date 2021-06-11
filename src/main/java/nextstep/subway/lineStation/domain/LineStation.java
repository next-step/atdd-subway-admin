package nextstep.subway.lineStation.domain;

import nextstep.subway.line.domain.Line;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stationId;
    private Long preStationId;
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private Line line;

    public LineStation() {
    }

    public LineStation(long stationId, long preStationId, int distance) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
    }

    public void lineBy(Line line) {
        this.line = line;
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LineStation that = (LineStation) object;
        return distance == that.distance &&
                Objects.equals(id, that.id) &&
                Objects.equals(stationId, that.stationId) &&
                Objects.equals(preStationId, that.preStationId) &&
                Objects.equals(line, that.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stationId, preStationId, distance, line);
    }
}
