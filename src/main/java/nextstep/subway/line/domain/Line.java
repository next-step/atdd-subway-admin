package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.message.ExceptionMessage;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    protected Line() {
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Line of(Long id, String name, String color, Station upStation, Station downStation) {
        validateNotNullAndNotEmpty(name);
        validateNotNullAndNotEmpty(color);
        validateStationsNotNull(upStation, downStation);
        validateStationsNotEqual(upStation, downStation);
        return new Line(id, name, color, upStation, downStation);
    }

    public static Line of(String name, String color, Station upStation, Station downStation) {
        return of(null, name, color, upStation, downStation);
    }

    private static void validateNotNullAndNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.REQUIRED);
        }
    }

    private static void validateStationsNotNull(Station upStation, Station downStation) {
        if (upStation == null || downStation == null) {
            throw new IllegalArgumentException(ExceptionMessage.REQUIRED);
        }
    }

    private static void validateStationsNotEqual(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(ExceptionMessage.UP_STATION_EQUALS_DOWN_STATION);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void update(String name, String color) {
        validateNotNullAndNotEmpty(name);
        validateNotNullAndNotEmpty(color);
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && name.equals(line.name) && color.equals(line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
