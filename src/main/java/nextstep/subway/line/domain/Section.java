package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;
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
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public List<Station> upAndDownStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void update(Section section) {
        this.upStation = section.upStation;
        this.downStation = section.downStation;
        this.distance = section.distance;
    }

    public boolean isNotEqualsStation() {
        return !this.upStation.equals(this.downStation);
    }

    public boolean isZeroDistance() {
        return distance <= 0;
    }

    public boolean isSameUpStation(Station target) {
        return this.upStation.equals(target);
    }

    public boolean isSameDownStation(Station target) {
        return this.downStation.equals(target);
    }

    public void changeDistance(int targetDistance) {
        if (targetDistance >= this.distance) {
            throw new IllegalArgumentException();
        }
        this.distance -= targetDistance;
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
