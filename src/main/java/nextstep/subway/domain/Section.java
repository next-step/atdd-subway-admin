package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

import static nextstep.subway.common.Messages.DISTANCE_LENGTH_ERROR;
import static nextstep.subway.common.Messages.DISTANCE_MINIMUM_LENGTH_ERROR;

@Entity
public class Section {
    private static int MINIMUM_LENGTH = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(int distance, Station upStation, Station downStation) {
        validateSection(distance);
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validateSection(int distance) {
        if (MINIMUM_LENGTH > distance) {
            throw new IllegalArgumentException(DISTANCE_MINIMUM_LENGTH_ERROR);
        }
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
        return Objects.equals(getUpStation(), section.getUpStation())
                && Objects.equals(getDownStation(), section.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUpStation(), getDownStation());
    }
}
