package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.exception.IllegalDistanceError;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Optional;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Station upStation;

    @OneToOne
    private Station downStation;

    private int distance;

    @ManyToOne
    private Line line;

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section() {

    }

    public static Section mergeSections(Section sameUpStation, Section sameDownStation) {
        return new Section(sameDownStation.upStation, sameUpStation.downStation, sameUpStation.distance + sameDownStation.distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalDistanceError();
        }
        this.distance = distance;
    }

    public boolean hasStation(Station station) {
        return station.equals(upStation) || station.equals(downStation);
    }

    public void insertNewDownStation(Station upStation, int distance) {
        this.downStation = upStation;
        setDistance(this.distance - distance);
    }

    public void insertNewUpStation(Station downStation, int distance) {
        this.upStation = downStation;
        setDistance(this.distance - distance);
    }

    public boolean hasSameDownStation(Section newSection) {
        return this.downStation == newSection.downStation;
    }

    public boolean hasSameStation(Section newSection) {
        return this.upStation.equals(newSection.upStation) && this.downStation.equals(newSection.downStation);
    }

    public boolean hasSameUpStation(Section newSection) {
        return this.upStation == newSection.upStation;
    }

    public void setSameLine(Optional<Section> sameUpStation) {
        this.line = sameUpStation.get().line;
    }
}
