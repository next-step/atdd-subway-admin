package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

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

    public Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void connectLine(Line line) {
        this.line = line;
    }

    public void addMerge(Section section) {
        if (section.isSameUpStation(upStation)) {
            changeUpStation(section.getDownStation());
            distance.minus(section.getDistance());
        }
        if (section.isSameDownStation(downStation)) {
            changeDownStation(section.getUpStation());
            distance.minus(section.getDistance());
        }
    }

    public void deleteMerge(Section section) {
        changeDownStation(section.getDownStation());
        distance.plus(section.getDistance());
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public boolean isSameAnyStation(Station station) {
        return getStations().contains(station);
    }

    public boolean isSameUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return downStation.equals(station);
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
