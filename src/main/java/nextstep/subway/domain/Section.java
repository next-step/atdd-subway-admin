package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "upstation_id")
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "downstation_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {

    }

    public Section(Station upStation, Station downStation, long distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, long distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public List<Station> getLineStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(final Line line) {
        this.line = line;
    }

    public Distance getDistance() {
        return distance;
    }

    public void changeStationInfo(Section section) {
        validDistanceCheck(section.getDistance());
        if (upStation.equals(section.getUpStation())) {
            changeUpStation(section.getDownStation());
            changeDistance(section.getDistance());
        }
        if (downStation.equals(section.getDownStation())) {
            changeDownStation(section.getUpStation());
            changeDistance(section.getDistance());
        }
    }

    private void validDistanceCheck(Distance newDistance) {
        if (!distance.isValidDistance(newDistance.getDistance())) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 추가할 수 없음");
        }
    }

    private void changeUpStation(Station downStation) {
        this.upStation = downStation;
    }

    private void changeDownStation(Station upStation) {
        this.downStation = upStation;
    }

    private void changeDistance(Distance newDistance) {
        this.distance = new Distance(this.distance.getDistance() - newDistance.getDistance());
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
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(
                upStation, section.upStation) && Objects.equals(downStation, section.downStation)
                && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance, line);
    }
}
