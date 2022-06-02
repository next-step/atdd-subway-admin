package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne()
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public List<Station> allStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void updateUpStation(Section section) {
        updateDistance(section.getDistance());
        this.upStation = section.getDownStation();
    }

    public void updateDownStation(Section section) {
        updateDistance(section.getDistance());
        this.downStation = section.getUpStation();
    }

    public boolean duplicateUpDownStations(Section section) {
        return this.upStation.equals(section.getUpStation()) && this.downStation.equals(section.getDownStation());
    }

    public boolean containsUpDownStations(Section section) {
        List<Station> stations = allStations();
        return stations.contains(section.getUpStation()) || stations.contains(section.getDownStation());
    }

    private void updateDistance(Distance distance) {
        setDistance(this.distance.subtract(distance));
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
        return distance;
    }

    private void setDistance(Distance distance) {
        this.distance = distance;
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
        return id != null ? id.hashCode() : 0;
    }

}
