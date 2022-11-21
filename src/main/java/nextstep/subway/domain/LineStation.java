package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

import static nextstep.subway.common.ErrorMessage.LINE_NOT_NULL;
import static nextstep.subway.common.ErrorMessage.STATION_NOT_NULL;

@Entity
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    protected LineStation() {
    }

    private LineStation(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    public static LineStation of(Line line, Station station) {
        validateLineIsNull(line);
        validateStationIsNull(station);
        return new LineStation(line, station);
    }

    private static void validateStationIsNull(Station station) {
        if (Objects.isNull(station)) {
            throw new IllegalArgumentException(STATION_NOT_NULL.getMessage());
        }
    }

    private static void validateLineIsNull(Line line) {
        if (Objects.isNull(line)) {
            throw new IllegalArgumentException(LINE_NOT_NULL.getMessage());
        }
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
