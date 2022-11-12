package nextstep.subway.section.domain;

import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    private static final int MAX_INVALID_DISTANCE = 0;

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

    private int distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        validateStations(upStation, downStation);
        validateDistance(distance);
        return new Section(upStation, downStation, distance);
    }

    public void addTo(Line line) {
        if (line.equals(this.line)) {
            return;
        }
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void update(Station station, int distance) {
        validateStationIsNotNull(station);
        validateDistance(distance);
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

    private static void validateDistance(int distance) {
        if (distance <= MAX_INVALID_DISTANCE) {
            throw new IllegalArgumentException(
                    String.format(ExceptionMessage.INVALID_SECTION_DISTANCE, distance)
            );
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
