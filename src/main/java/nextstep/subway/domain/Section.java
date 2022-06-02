package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    private static final String DISTANCE_LENGTH_ERROR = "역 사이 길이보다 크거나 같을 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(int distance, Station upStation, Station downStation) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Section of(int distance, Station upStation, Station downStation) {
        return new Section(distance, upStation, downStation);
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

    public boolean isEqualsUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualsDownStation(Station station) {
        return downStation.equals(station);
    }

    public void updateUpStation(Station updateStation, int updateDistance) {
        validateDistance(updateDistance);
        upStation = updateStation;
        distance = distance - updateDistance;
    }

    public void updateDownStation(Station station) {
        downStation = station;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    private void validateDistance(int newDistance) {
        if (distance <= newDistance) {
            throw new IllegalArgumentException(DISTANCE_LENGTH_ERROR);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
