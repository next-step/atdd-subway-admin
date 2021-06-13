package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Entity
public class Section {

    public static final Section EMPTY = new Section();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @ManyToOne
    private Line line;

    private int distance;

    public Section() {}

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private Section(Long id, Station upStation, Station downStation, Line line, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public static Section of(Long id, Station upStation, Station downStation, Line line, int distance) {
        return new Section(id, upStation, downStation, line, distance);
    }

    public StationResponse upStationToReponse() {
        return StationResponse.of(upStation);
    }

    public StationResponse downStationToReponse() {
        return StationResponse.of(downStation);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public int getDistance() {
        return distance;
    }

    public boolean hasDownStation(Section compareSection) {
        return this.downStation.equals(compareSection.upStation) || this.downStation.equals(compareSection.downStation);
    }

    public boolean hasUpStation(Section compareSection) {
        return this.upStation.equals(compareSection.upStation) || this.upStation.equals(compareSection.downStation);
    }

    public boolean sameUpStation(Section compareSection) {
        return compareSection.upStation.equals(this.upStation);
    }

    public boolean sameDownStation(Section compareSection) {
        return compareSection.downStation.equals(this.downStation);
    }

    public boolean isOverDistance(Section compareSection) {
        return this.distance <= compareSection.distance;
    }

    public void updateUpStation(Section compareSection) {
        this.upStation = compareSection.downStation;
    }

    public void updateMinusDistance(Section compareSection) {
        this.distance -= compareSection.distance;
    }

    public void updateDownStation(Section compareSection) {
        this.downStation = compareSection.upStation;
    }

    public boolean isUpStationWithDown(Section beforeSection) {
        return this.upStation.equals(beforeSection.downStation);
    }

    public boolean isDownStationWithUp(Section beforeSection) {
        return this.downStation.equals(beforeSection.upStation);
    }

    public boolean sameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean sameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void connectDistance(Section afterSection) {
        this.distance += afterSection.distance;
    }

    public Long upStationId() {
        return upStation.getId();
    }

    public Long downStationId() {
        return downStation.getId();
    }

    public void updateDownStationToDown(Section afterSection) {
        this.downStation = afterSection.downStation;
    }

}
