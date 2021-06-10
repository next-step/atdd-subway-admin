package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    private int distance;

    public Section() {}

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
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

    public boolean hasDownStation(Section compareSection) {
        return this.downStation.equals(compareSection.upStation) || this.downStation.equals(compareSection.downStation);
    }

    public boolean hasUpStation(Section compareSection) {
        return this.upStation.equals(compareSection.upStation) || this.upStation.equals(compareSection.downStation);
    }

    public boolean sameUpStaion(Section compareSection) {
        return compareSection.upStation.equals(this.upStation);
    }

    public boolean sameDownStaion(Section compareSection) {
        return compareSection.downStation.equals(this.downStation);
    }

    public boolean isOverDistance(Section compareSection) {
        return this.distance <= compareSection.distance;
    }

    public void updateUpStation(Section compareSection) {
        this.upStation = compareSection.downStation;
    }

    public void updateDistance(Section compareSection) {
        this.distance -= compareSection.distance;
    }

    public void updateDownStation(Section compareSection) {
        this.downStation = compareSection.upStation;
    }

    public boolean isStartStation(Section startSection) {
        return this.upStation.equals(startSection.downStation);
    }

    public boolean isEndStation(Section findSection) {
        return this.downStation.equals(findSection.upStation);
    }

}
