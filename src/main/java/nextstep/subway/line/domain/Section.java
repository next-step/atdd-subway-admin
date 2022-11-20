package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long lineId;
    @ManyToOne
    private Station upStation;
    @ManyToOne
    private Station downStation;
    @Embedded
    private Distance distance;

    protected Section() {
    }


    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(long id, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void relocate(Section newSection) {
        relocateUpStation(newSection);
        relocateDownStation(newSection);
    }

    public Section merge(Section other) {
        return new Section(this.upStation, other.downStation, distance.sum(other.distance));
    }

    public boolean hasUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean hasDownStation(Station station) {
        return this.downStation.equals(station);
    }

    private void relocateUpStation(Section newSection) {
        if (this.upStation.equals(newSection.upStation)) {
            this.upStation = newSection.getDownStation();
            this.distance.modifyDistance(newSection.getDistance());
        }
    }

    private void relocateDownStation(Section newSection) {
        if (this.downStation.equals(newSection.downStation)) {
            this.downStation = newSection.getUpStation();
            this.distance.modifyDistance(newSection.getDistance());
        }
    }



    public List<Station> getStations() {
        return Arrays.asList(this.upStation, this.downStation);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance
            && Objects.equals(id, section.id)
            && Objects.equals(lineId, section.lineId)
            && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStation, downStation, distance);
    }
}
