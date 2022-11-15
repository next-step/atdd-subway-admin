package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;
    @Embedded
    private Distance distance;

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    protected Section() {}

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> findStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public void update(Section newSection) {
        if (isEqualUpStation(newSection.upStation)) {
            updateUpStation(newSection);
        }

        if (isEqualDownStation(newSection.downStation)) {
            updateDownStation(newSection);
        }
    }

    public boolean isEqualUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualDownStation(Station station) {
        return downStation.equals(station);
    }

    public void updateUpStation(Section newSection) {
        upStation = newSection.downStation;
        distance = distance.subtract(newSection.distance);
    }

    public void updateDownStation(Section newSection) {
        downStation = newSection.upStation;
        distance = distance.subtract(newSection.distance);
    }

    public Section merge(Section nextSection) {
        Distance newDistance = distance.add(nextSection.distance);
        Section section = new Section(upStation, nextSection.getDownStation(), newDistance);
        section.addLine(line);
        return section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
