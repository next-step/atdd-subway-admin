package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.valueOf(distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public List<Station> toStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void addInsideOfSection(Section sectionToAdd) {
        if (isEqualsToUpStation(sectionToAdd)) {
            updateUpStation(sectionToAdd);
        }
        if (isEqualsToDownStation(sectionToAdd)) {
            updateDownStation(sectionToAdd);
        }
    }

    private void updateUpStation(Section sectionToAdd) {
        this.distance = distance.minus(sectionToAdd.getDistance());
        this.upStation = sectionToAdd.getDownStation();
    }

    private void updateDownStation(Section sectionToAdd) {
        this.distance = distance.minus(sectionToAdd.getDistance());
        this.downStation = sectionToAdd.getUpStation();
    }

    public Distance getDistance() {
        return distance;
    }

    public boolean isEqualsToUpStation(Section sectionToAdd) {
        return upStation.equals(sectionToAdd.getUpStation());
    }

    public boolean isEqualsToDownStation(Section sectionToAdd) {
        return downStation.equals(sectionToAdd.getDownStation());
    }

    public void addStations(List<Station> stations) {
        stations.add(upStation);
        stations.add(downStation);
    }

    public boolean isNextSection(Section previousSection) {
        return previousSection.isEqualsDownStationAndUpStation(upStation);
    }

    private boolean isEqualsDownStationAndUpStation(Station upStation) {
        return downStation.equals(upStation);
    }

    public void addNextStation(List<Station> stations) {
        stations.add(downStation);
    }

    public boolean isPreviousSection(Section nextSection) {
        return nextSection.isEqualsUpStationAndDownStation(downStation);
    }

    private boolean isEqualsUpStationAndDownStation(Station downStation) {
        return upStation.equals(downStation);
    }

    public boolean isDownStationEqualsToStation(Station station) {
        return station.equals(downStation);
    }

    public boolean isUpStationEqualsToStation(Station station) {
        return station.equals(upStation);
    }
}
