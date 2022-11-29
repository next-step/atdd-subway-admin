package nextstep.subway.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = Station.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk__station_of_line_station"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Station station;
    @ManyToOne(targetEntity = Line.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk__line_of_line_station"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Line line;

    public LineStation() {
    }

    public LineStation(Station station, Line line) {
        this.station = station;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Line getLine() {
        return line;
    }
}
