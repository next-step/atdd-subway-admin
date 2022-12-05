package nextstep.subway.domain.line;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.domain.BaseEntity;
import nextstep.subway.domain.station.Station;

@Entity
public class LineStation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_line_station_station"))
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_station_id", foreignKey = @ForeignKey(name = "fk_line_station_pre_station"))
    private Station preStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private Line line;

    public LineStation() {
    }

    public LineStation(Station station, Station preStation, int distance, Line line) {
        this.station = station;
        this.preStation = preStation;
        this.distance = distance;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Station getPreStation() {
        return preStation;
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
        if (!line.getLineStations().contains(this)) {
            line.getLineStations().add(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStation that = (LineStation) o;
        return distance == that.distance && Objects.equals(id, that.id) && Objects.equals(station,
                that.station) && Objects.equals(preStation, that.preStation) && Objects.equals(line,
                that.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, station, preStation, distance, line);
    }
}
