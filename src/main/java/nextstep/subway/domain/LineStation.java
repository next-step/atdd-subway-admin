package nextstep.subway.domain;
import static nextstep.subway.message.ErrorMessage.LINE_STATION_HAS_LINE_ESSENTIAL;
import static nextstep.subway.message.ErrorMessage.LINE_STATION_HAS_STATION_ESSENTIAL;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "lineId")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "stationId")
    private Station station;

    protected LineStation() {

    }

    public LineStation(Line line, Station station) {
        valid(line, station);
        this.line = line;
        this.station = station;
    }

    public Line getLine() {
        return line;
    }

    public long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    private void valid(Line line, Station station) {
        validLine(line);
        validStation(station);
    }

    private void validLine(Line line) {
        if (line == null) {
            throw new IllegalArgumentException(LINE_STATION_HAS_LINE_ESSENTIAL.toMessage());
        }
    }

    private void validStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException(LINE_STATION_HAS_STATION_ESSENTIAL.toMessage());
        }
    }
}
