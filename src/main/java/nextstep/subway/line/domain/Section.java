package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
        this.line = line;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance, line);
    }

    public void updateUpSection(Section section) {
        this.upStation = section.downStation;
        this.distance = this.distance.minus(section.distance);
    }

    public void updateDownSection(Section section) {
        this.downStation = section.upStation;
        this.distance = this.distance.minus(section.distance);
    }

    public boolean isEqualLine(Line line) {
        return this.line.equals(line);
    }

    public boolean hasEqualUpStation(Section section) {
        return this.upStation == section.upStation;
    }

    public boolean hasEqualDownStation(Section section) {
        return this.downStation == section.downStation;
    }

    public boolean isContainsStation(Section section) {
        if (this.upStation.equals(section.upStation)
                || this.upStation.equals(section.downStation)
                || this.downStation.equals(section.upStation)
                || this.downStation.equals(section.downStation)) {
            return true;
        }
        return false;
    }

    public boolean hasEqualDownStation(Station station) {
        return this.downStation == station;
    }

    public boolean hasEqualUpStation(Station station) {
        return this.upStation == station;
    }

    public void updateForRemove(Section section) {
        this.downStation = section.downStation;
        this.distance = this.distance.plus(section.distance);
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
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

    public Distance getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance, line);
    }
}
