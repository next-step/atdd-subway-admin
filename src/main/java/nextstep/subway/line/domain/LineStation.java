package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.wrappers.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class LineStation extends BaseEntity {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_station_to_line"))
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_station_id", foreignKey = @ForeignKey(name = "fk_pre_station_to_line"))
    private Station preStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private Line line;

    public LineStation() {
    }

    public LineStation(Station station, Station preStation, int distance) {
        this.station = station;
        this.preStation = preStation;
        this.distance = new Distance(distance);
    }

    public LineStation(Station station, Station preStation, int distance, Line line) {
        this.station = station;
        this.preStation = preStation;
        this.distance = new Distance(distance);
        this.line = line;
    }

    public void lineBy(Line line) {
        this.line = line;
    }

    public boolean isNullPreStation() {
        return Objects.isNull(preStation);
    }

    public boolean isNextLineStation(LineStation lineStation) {
        return Objects.nonNull(preStation) && Objects.equals(preStation.getId(), lineStation.getStation().getId());
    }

    public boolean isSamePreStation(LineStation lineStation) {
        return Objects.nonNull(this.preStation) && Objects.equals(this.preStation.getId(), lineStation.preStation.getId());
    }

    public boolean isSameStation(LineStation lineStation) {
        return this.station.getId() == lineStation.station.getId();
    }

    public boolean isSameStation(Station other) {
        return station.isSameId(other);
    }

    public Distance creatNewDistance(LineStation lineStation) {
        return this.distance.subtractionDistance(lineStation.distance);
    }

    public boolean isSame(LineStation other) {
        if (Objects.isNull(preStation)) {
            return Objects.isNull(other.preStation) && (station.isSameId(other.station));
        }
        return (preStation.isSameId(other.preStation)) && (station.isSameId(other.station));
    }

    public boolean isLastLineStation(LineStation lineStation) {
        return this.station.isSameId(lineStation.preStation);
    }

    public void update(Station station, Station preStation, Distance distance) {
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

    public Line getLine() {
        return line;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LineStation that = (LineStation) object;
        return Objects.equals(id, that.id) &&
                Objects.equals(station, that.station) &&
                Objects.equals(preStation, that.preStation) &&
                Objects.equals(distance, that.distance) &&
                Objects.equals(line, that.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, station, preStation, distance, line);
    }

    public boolean isNotContainStation(LineStation lineStation) {
        if (Objects.isNull(preStation)) {
            return !(Objects.nonNull(lineStation.preStation) || station.isSameId(lineStation.preStation) || station.isSameId(lineStation.station));
        }
        return !(preStation.isSameId(lineStation.preStation)
                || preStation.isSameId(lineStation.station)
                || station.isSameId(lineStation.preStation)
                || station.isSameId(lineStation.station));
    }
}
