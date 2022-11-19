package nextstep.subway.domain;

import nextstep.subway.exception.SectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static nextstep.subway.exception.SectionExceptionMessage.LESS_THEN_ZERO_DISTANCE;

@Entity
public class Section extends BaseEntity {

    @Id
    @Column(name = "section_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;
    private long distance;

    protected Section() {}

    public Section(Line line, Station upStation, Station downStation, long distance) {
        validateDistance(distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(long distance) {
        if (distance <= 0) {
            throw new SectionException(LESS_THEN_ZERO_DISTANCE.getMessage());
        }
    }

    public List<Station> getStations() {
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    public long getDistance() {
        return distance;
    }

    public boolean isShortDistance(Section section) {
        return distance <= section.getDistance();
    }

    public boolean isSameUpStation(Section newSection) {
        return upStation.equals(newSection.getUpStation());
    }

    public boolean isSameDownStation(Section newSection) {
        return downStation.equals(newSection.getDownStation());
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isComponentAllOfStations(List<Station> stations) {
        return stations.contains(upStation) && stations.contains(downStation);
    }

    public boolean isComponentAnyOfStations(List<Station> stations) {
        return stations.contains(upStation) || stations.contains(downStation);
    }

    public void modifyUpStation(Section newSection) {
        this.upStation = newSection.getDownStation();
        this.distance -= newSection.getDistance();
    }

    public void modifyDownStation(Section newSection) {
        this.downStation = newSection.getUpStation();
        this.distance -= newSection.getDistance();
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);

    }

    public boolean isSameUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return downStation.equals(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance &&
                id.equals(section.id) &&
                line.equals(section.line) &&
                upStation.equals(section.upStation) &&
                downStation.equals(section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
