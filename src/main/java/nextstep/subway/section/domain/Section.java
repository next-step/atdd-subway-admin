package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = Optional.ofNullable(upStation).orElseThrow(() ->
                new IllegalArgumentException("상행역으로 Null을 입력할 수 없습니다."));
        this.downStation = Optional.ofNullable(downStation).orElseThrow(() ->
                new IllegalArgumentException("하행역으로 Null을 입력할 수 없습니다."));
        validateSameStations(upStation, downStation);
        validateDistance(distance);
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this(upStation, downStation, distance);
        this.line = Optional.ofNullable(line).orElseThrow(() ->
                new IllegalArgumentException("노선으로 Null을 입력할 수 없습니다."));
        this.line.addSection(this);
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

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return this.line;
    }

    public List<Station> getStations() {
        return new ArrayList<>(Arrays.asList(this.upStation, this.downStation));
    }

    public boolean containsStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public Section updateSection(Section section) {
        this.upStation = section.getUpStation();
        this.downStation = section.getDownStation();
        this.distance = section.getDistance();
        return this;
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

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 거리는 1 이상이어야 합니다.");
        }
    }
}
