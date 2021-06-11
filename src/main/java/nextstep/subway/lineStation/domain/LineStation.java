package nextstep.subway.lineStation.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_lineStation_to_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_lineStation_to_station"))
    private Station station;

    public LineStation() {
    }

    public LineStation(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }
}
