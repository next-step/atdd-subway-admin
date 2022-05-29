package nextstep.subway.section.domain;

import static nextstep.subway.station.domain.exception.StationExceptionMessage.CANNOT_EQUALS_UP_STATION_WITH_DOWN_STATION;
import static nextstep.subway.station.domain.exception.StationExceptionMessage.DISTANCE_IS_NOT_NULL;
import static nextstep.subway.station.domain.exception.StationExceptionMessage.DOWN_STATION_IS_NOT_NULL;
import static nextstep.subway.station.domain.exception.StationExceptionMessage.UP_STATION_IS_NOT_NULL;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @OneToOne
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {}

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private Section(Long id, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        validateSection(upStation, downStation, distance);
        return new Section(upStation, downStation, distance);
    }

    public static Section of(Long id, Station upStation, Station downStation, Distance distance) {
        validateSection(upStation, downStation, distance);
        return new Section(id, upStation, downStation, distance);
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Distance getDistance() {
        return this.distance;
    }

    public void registerLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return this.line;
    }

    public boolean isEqualsUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isEqualsDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public boolean isGreaterThanOrEqualsDistance(Section middleSection) {
        return this.distance.isGraterThanOrEquals(middleSection.getDistance());
    }

    public void reduceDistanceByDistance(Distance distance) {
        this.distance.minus(distance);
    }

    public void plusDistanceByDistance(Distance distance) {
        this.distance.plus(distance);
    }

    public Long getId() {
        return this.id;
    }

    private static void validateSection(Station upStation, Station downStation, Distance distance) {
        if (Objects.isNull(upStation)) {
            throw new IllegalArgumentException(UP_STATION_IS_NOT_NULL.getMessage());
        }

        if (Objects.isNull(downStation)) {
            throw new IllegalArgumentException(DOWN_STATION_IS_NOT_NULL.getMessage());
        }

        if (Objects.isNull(distance)) {
            throw new IllegalArgumentException(DISTANCE_IS_NOT_NULL.getMessage());
        }

        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(CANNOT_EQUALS_UP_STATION_WITH_DOWN_STATION.getMessage());
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
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId()) && Objects.equals(
            getDistance(), section.getDistance()) && Objects.equals(getUpStation(),
            section.getUpStation()) && Objects.equals(getDownStation(),
            section.getDownStation()) && Objects.equals(getLine(), section.getLine());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
