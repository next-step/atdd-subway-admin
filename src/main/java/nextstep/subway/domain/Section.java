package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this(upStation, downStation, distance);
        toLine(line);
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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

    public void updateUpStation(Station station, Distance newDistance) {
        validateDistance(newDistance);
        upStation = station;
        distance = distance.minus(newDistance);
    }

    public void updateDownStation(Station station, Distance newDistance) {
        validateDistance(newDistance);
        downStation = station;
        distance = distance.minus(newDistance);
    }

    private void validateDistance(Distance newDistance) {
        if (distance.isLessThanOrEqual(newDistance)) {
            throw new InvalidSectionDistanceException();
        }
    }

    public boolean equalUpStation(Station station) {
        return Objects.equals(upStation, station);
    }

    public boolean equalUpStation(Section section) {
        return Objects.equals(upStation, section.getUpStation());
    }

    public boolean equalDownStation(Station station) {
        return Objects.equals(downStation, station);
    }

    public boolean equalDownStation(Section section) {
        return Objects.equals(downStation, section.getDownStation());
    }


}
