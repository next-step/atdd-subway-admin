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

    private Section(Station upStation, Station downStation, Distance distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
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

    public void changeStationInfo(Section section) {
        if (upStation.equals(section.upStation)) {
            this.upStation = section.downStation;
            this.distance.subtractDistance(section.distance);
        }
        if (downStation.equals(section.downStation)) {
            this.downStation = section.upStation;
            this.distance.subtractDistance(section.distance);
        }
    }

    public Section combineSections(Section section) {
        return new Section(this.upStation, section.downStation, this.distance.addDistance(section.distance), this.line);
    }

    public boolean hasAnyStations(Section section) {
        return hasStations(section.upStation) || hasStations(section.downStation);
    }

    public boolean hasAllStations(Section section) {
        return hasStations(section.upStation) && hasStations(section.downStation);
    }

    public boolean hasStations(Station station) {
        return hasUpStation(station) || hasDownStation(station);
    }

    public boolean hasUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean hasDownStation(Station station) {
        return downStation.equals(station);
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
