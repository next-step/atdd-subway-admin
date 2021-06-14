package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
public class Section extends BaseEntity {
    private static final String STATION_NOT_FOUND = "역을 찾을 수 없습니다.";
    private static final String INVALID_DISTANCE = "잘못된 거리 입력입니다";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateStation(upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Section(Section section) {
        this.line = section.line;
        this.upStation = section.upStation;
        this.downStation = section.downStation;
        this.distance = section.distance;
    }

    private void validateStation(Station upStation, Station downStation) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new StationNotFoundException(STATION_NOT_FOUND);
        }
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void changeDownStation(Section section) {
        this.downStation = section.getUpStation();
        this.distance = this.distance.minus(section.distance);
    }

    public void changeUpStation(Section section) {
        this.upStation = section.getDownStation();
        this.distance = this.distance.minus(section.distance);
    }

    public void changeDownStationEdge(Section section) {
        this.downStation = section.getUpStation();
        this.distance = section.distance;
    }

    public void changeUpStationEdge(Section section) {
        this.upStation = section.getDownStation();
        this.distance = section.distance;
    }

    public boolean containsAllStations(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }

    public boolean isSameUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean isSameUpStation(Long stationId) {
        return upStation.isStationIdMatch(stationId);
    }

    public boolean isSameDownStation(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public boolean isSameDownStation(Long stationId) {
        return downStation.isStationIdMatch(stationId);
    }

    public boolean containsNoneStations(Section section) {
        return getStations().stream()
                .noneMatch(v -> v.equals(section.downStation) || v.equals(section.upStation));
    }

    public List<Station> getStations() {
        return new LinkedList<>(Arrays.asList(upStation, downStation));
    }

    public Stream<Station> getProcessStations() {
        return Stream.of(upStation, downStation);
    }

    public boolean hasStationId(Long stationId) {
        return upStation.isStationIdMatch(stationId) || downStation.isStationIdMatch(stationId);
    }

    public boolean isLastSectionBefore(Section section) {
        return downStation.equals(section.downStation);
    }

    public boolean isLastSectionNext(Section section) {
        return downStation.equals(section.upStation);
    }

    public boolean isFirstSectionBefore(Section section) {
        return upStation.equals(section.downStation);
    }

    public boolean isFirstSectionNext(Section section) {
        return upStation.equals(section.upStation);
    }

    public void changeUpward(Section section) {
        this.downStation = section.downStation;
        this.distance = distance.plus(section.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation()) && Objects.equals(distance, section.distance) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getUpStation(), getDownStation(), distance, line);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", line=" + line +
                '}';
    }
}
