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
        if (upStation.equals(section.getUpStation())) {
            changeUpStation(section.getDownStation());
            changeDistance(distance.minus(section.getDistance()));
        }
        if (downStation.equals(section.getDownStation())) {
            changeDownStation(section.getUpStation());
            changeDistance(distance.minus(section.getDistance()));
        }
    }

    public void deleteMerge(Section section) {
        changeDownStation(section.getDownStation());
        changeDistance(distance.plus(section.getDistance()));
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void changeDistance(Distance distance) {
        this.distance = distance;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public boolean equalAnyStation(Station station) {
        return getStations().contains(station);
    }

    public boolean equalUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean equalDownStation(Station station) {
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
