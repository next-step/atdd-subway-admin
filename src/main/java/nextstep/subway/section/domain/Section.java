package nextstep.subway.section.domain;

import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        validateStations(upStation, downStation);
        return new Section(upStation, downStation, Distance.from(distance));
    }

    public void addTo(Line line) {
        if (line.equals(this.line)) {
            return;
        }
        this.line = line;
    }

    public void removeFromLine() {
        this.line = null;
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

    public void update(Station station, Distance distance) {
        validateStationIsNotNull(station);
        this.upStation = station;
        this.distance = distance;
    }

    private static void validateStations(Station upStation, Station downStation) {
        validateStationIsNotNull(upStation);
        validateStationIsNotNull(downStation);

        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(ExceptionMessage.UP_STATION_EQUALS_DOWN_STATION);
        }
    }

    private static void validateStationIsNotNull(Station station) {
        if (station == null) {
            throw new IllegalArgumentException(ExceptionMessage.REQUIRED);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return upStation.equals(section.upStation) &&
                downStation.equals(section.downStation) &&
                Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, line);
    }
}
