package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Distance distance, Line line) {
        this(upStation, downStation, distance);
        this.line = line;
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this(upStation, downStation);
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(upStation, downStation);
        this.distance = new Distance(distance);
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this(upStation, downStation, distance);
        this.line = Optional.ofNullable(line).orElseThrow(() ->
                new IllegalArgumentException("노선으로 Null을 입력할 수 없습니다."));
        this.line.addSection(this);
    }

    public Section(Station upStation, Station downStation) {
        this.upStation = Optional.ofNullable(upStation).orElseThrow(() ->
                new IllegalArgumentException("상행역으로 Null을 입력할 수 없습니다."));
        this.downStation = Optional.ofNullable(downStation).orElseThrow(() ->
                new IllegalArgumentException("하행역으로 Null을 입력할 수 없습니다."));
        validateSameStations(upStation, downStation);
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
        return this.distance;
    }

    public Line getLine() {
        return this.line;
    }

    public List<Station> getStations() {
        return new ArrayList<>(Arrays.asList(this.upStation, this.downStation));
    }

    public void updateDistance(Section section, Distance totalDistance) {
        this.distance.plusDistance(section.distance).minusDistance(totalDistance);
        section.distance.minusDistance(this.distance);
    }

    public void updateDistance(Distance totalDistance) {
        this.distance.minusDistance(totalDistance);
    }

    public void updateStations(Section updateSection) {
        this.upStation = updateSection.upStation;
        this.downStation = updateSection.downStation;
    }

    public boolean hasSameDistanceAs(Distance targetDistance) {
        return this.distance.isEqualTo(targetDistance);
    }

    public boolean hasSameDistanceAs(int distance) {
        return this.distance.isEqualTo(distance);
    }

    public void addDistanceTo(Distance targetDistance) {
        targetDistance.plusDistance(this.distance);
    }

    public boolean hasDistanceShorterThanOrEqualTo(Distance targetDistance) {
        return targetDistance.isGreaterThanOrEqualTo(this.distance);
    }

    public boolean hasLongerDistanceThan(Distance targetDistance) {
        return this.distance.isGreaterThan(targetDistance);
    }

    public boolean hasSameUpStationAs(Station targetStation) {
        return this.upStation.equals(targetStation);
    }

    public boolean hasSameUpStationAsUpStationOf(Section targetSection) {
        return this.upStation.equals(targetSection.upStation);
    }

    public boolean hasSameUpStationAsDownStationOf(Section targetSection) {
        return this.upStation.equals(targetSection.downStation);
    }

    public boolean hasSameDownStationAsUpStationOf(Section targetSection) {
        return this.downStation.equals(targetSection.upStation);
    }

    public boolean hasSameDownStationAsDownStationOf(Section targetSection) {
        return this.downStation.equals(targetSection.downStation);
    }

    public void sumDistanceWith(Section section) {
        this.distance.plusDistance(section.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance &&
                Objects.equals(id, section.id) &&
                Objects.equals(upStation, section.upStation) &&
                Objects.equals(downStation, section.downStation) &&
                Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation.toString() +
                ", downStation=" + downStation.toString() +
                ", distance=" + distance +
                ", lineId=" + line.getId() +
                '}';
    }

    private void validateSameStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행, 하행역은 동일한 역일 수 없습니다.");
        }
    }
}
