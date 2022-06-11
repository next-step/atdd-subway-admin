package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationException;
import nextstep.subway.station.exception.StationExceptionType;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;
    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final long distance) {
        validate(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static void validate(final Station upStation, final Station downStation) {
        if (upStation == null || downStation == null) {
            throw new StationException(StationExceptionType.NOT_FOUND_STATION);
        }
    }

    public static Section of(final Station upStation, final Station downStation, final long distance) {
        return new Section(upStation, downStation, distance);
    }

    public boolean isEqualsUpStation(final Section section) {
        return this.upStation.equals(section.upStation);
    }

    public void updateUpStation(final Section section) {
        this.upStation = section.downStation;
        this.distance.updateDistance(section.getDistance());
    }

    public boolean isEqualsDownStation(final Section section) {
        return this.downStation.equals(section.downStation);
    }

    public void updateDownStation(final Section section) {
        this.downStation = section.upStation;
        this.distance = section.distance;
    }


    public void updateLine(final Line line) {
        this.line = line;
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

    public long getDistance() {
        return distance.getValue();
    }

    public Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}


