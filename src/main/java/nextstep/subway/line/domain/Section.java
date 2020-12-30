package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "lineId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @JoinColumn(name = "upStationId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @JoinColumn(name = "downStationId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        checkSameStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void checkSameStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역 하행역이 같습니다.");
        }
    }

    public void setLine(Line line) {
        this.line = line;
    }

    private void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    private void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void update(Section targetSection) {
        this.upStation = targetSection.upStation;
        this.downStation = targetSection.downStation;
        this.distance = targetSection.distance;
    }

    public void merge(Section targetSection, Station excludeStation) {
        if (isSameUpStation(excludeStation)) {
            this.changeUpStation(targetSection.upStation);
        }

        if (isSameDownStation(excludeStation)) {
            this.changeDownStation(targetSection.downStation);
        }
        sumDistance(targetSection.distance);
    }

    public boolean isZeroDistance() {
        return distance <= 0;
    }

    public boolean containStation(Station targetStation) {
        return isSameUpStation(targetStation) || isSameDownStation(targetStation);
    }

    public boolean isConnectable(Section targetSection) {
        return isSameUpStation(targetSection.downStation) || isSameDownStation(targetSection.upStation);
    }

    public void switchUpStationAndDistance(Section targetSection) {
        this.changeUpStation(targetSection.downStation);
        this.subtractDistance(targetSection.distance);
    }

    public void switchDownStationAndDistance(Section targetSection) {
        this.changeDownStation(targetSection.upStation);
        this.subtractDistance(targetSection.distance);
    }

    public boolean isSameUpStation(Section targetSection) {
        return this.upStation.equals(targetSection.upStation);
    }

    private boolean isSameUpStation(Station targetStation) {
        return this.upStation.equals(targetStation);
    }

    public boolean isSameDownStation(Section targetStation) {
        return this.downStation.equals(targetStation.downStation);
    }

    public boolean isSameDownStation(Station targetStation) {
        return this.downStation.equals(targetStation);
    }

    private void subtractDistance(int targetDistance) {
        if (isGraterThanDistance(targetDistance)) {
            throw new IllegalArgumentException();
        }
        this.distance -= targetDistance;
    }

    private boolean isGraterThanDistance(int targetDistance) {
        return targetDistance >= this.distance;
    }

    private void sumDistance(int targetDistance) {
        this.distance += targetDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return (upStation.equals(section.upStation) &&
                downStation.equals(section.downStation)) ||
                (upStation.equals(section.downStation) &&
                        downStation.equals(section.upStation));
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }
}
