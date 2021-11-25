package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.SectionDistanceException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    private static final String SECTION_DISTANCE_ERROR_MESSAGE = "기존 역 사이보다 크거나 같은면 등록할 수 없습니다.";
    private static final String DISTANCE_ERROR_MESSAGE = "거리는 0보다 커야 합니다.";
    private static final String SAME_STATION_ERROR_MESSAGE = "같은 역은 구간이 될 수 없습니다.";

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

    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validate(upStation, downStation, distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(Station upStation, Station downStation, int distance) {
        validateStation(upStation, downStation);
        validateDistance(distance);
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(DISTANCE_ERROR_MESSAGE);
        }
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_STATION_ERROR_MESSAGE);
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation, distance);
    }

    public void updateUpStationTo(Station downStation, int distance) {
        validateSectionDistance(distance);
        upStation = downStation;
        this.distance -= distance;
    }

    public void updateDownStationTo(Station upStation, int distance) {
        validateSectionDistance(distance);
        downStation = upStation;
        this.distance -= distance;
    }

    private void validateSectionDistance(int distance) {
        if (this.distance <= distance) {
            throw new SectionDistanceException(SECTION_DISTANCE_ERROR_MESSAGE);
        }
    }

    public boolean isDuplicate(Section section) {
        return upStation.equals(section.getUpStation()) && downStation.equals(section.getDownStation());
    }
}
