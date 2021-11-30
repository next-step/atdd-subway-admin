package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @ManyToOne
    private Line line;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        validateDuplicate(upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.from(distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance.value();
    }

    public boolean isEqualsUpStation(Station station) {
        return this.upStation.equals(station);
    }

    private static void validateDuplicate(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new BadRequestException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
