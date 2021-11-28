package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
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
}
