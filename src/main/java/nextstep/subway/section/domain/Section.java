package nextstep.subway.section.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7761205882129160224L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne(optional = false)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this(upStation, downStation, distance);
        this.line = line;
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this(upStation, downStation, new Distance(distance));
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section toLine(Line line) {
        this.line = line;
        return this;
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

    public boolean contains(Station station) {
        return station.equals(this.upStation) || station.equals(this.downStation);
    }

    public boolean isDuplicateSection(Station upStation, Station downStation) {
        return contains(upStation) && contains(downStation);
    }

    public boolean equalsUpStation(Station station) {
        return station.equals(this.upStation);
    }

    public boolean equalsDownStation(Station station) {
        return station.equals(this.downStation);
    }

    public Section updateUpStation(Station station) {
        this.upStation = station;
        return this;
    }

    public Section updateDownStation(Station upStation) {
        this.downStation = upStation;
        return this;
    }

    public Section updateDistance(int distance) {
        this.distance = new Distance(distance);
        return this;
    }

    public void minusDistance(int value) {
        distance = distance.minus(value);
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
