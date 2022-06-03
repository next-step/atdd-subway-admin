package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validateStation(upStation, downStation);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean equalUpStation(Station station) {
        return getUpStation().getId().equals(station.getId());
    }

    public boolean equalDownStation(Station station) {
        return getDownStation().getId().equals(station.getId());
    }

    public int getDistance() {
        return distance;
    }

    public void updateUpStation(Station station, int distance) {
        validateStation(this.upStation, station);
        this.distance = calculateDistance(distance);
        this.upStation = station;
    }

    public void updateDownStation(Station station, int distance) {
        validateStation(station, this.downStation);
        this.distance = calculateDistance(distance);
        this.downStation = station;
    }

    private int calculateDistance(int distance) {
        validateDistance(distance);
        return this.distance - distance;
    }

    private void validateDistance(int distance) {
        if (this.distance < distance) {
            throw new IllegalArgumentException("역 사이의 거리가 초과되었습니다.");
        }
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation.getId().equals(downStation.getId())) {
            throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return getDistance() == section.getDistance() &&
                Objects.equals(getId(), section.getId()) &&
                Objects.equals(getLine(), section.getLine()) &&
                Objects.equals(getUpStation(), section.getUpStation()) &&
                Objects.equals(getDownStation(), section.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLine(), getUpStation(), getDownStation(), getDistance());
    }
}
