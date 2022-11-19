package nextstep.subway.line;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import nextstep.subway.line.exception.IllegalDistanceException;
import nextstep.subway.station.Station;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    private Station upStation;
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    private Station downStation;
    private int distance;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public List<Station> stations() {
        return Arrays.asList(upStation, downStation);
    }

    public boolean hasRelation(Section newSection) {
        return hasSameUpStation(newSection)
            || this.upStation == newSection.downStation
            || this.downStation == newSection.upStation
            || hasSameDownStation(newSection);
    }

    private boolean hasSameUpStation(Section newSection) {
        return this.upStation == newSection.upStation;
    }

    private boolean hasSameDownStation(Section newSection) {
        return this.downStation == newSection.downStation;
    }

    public boolean hasSameUpOrDownStation(Section newSection) {
        return hasSameUpStation(newSection) || hasSameDownStation(newSection);
    }

    public void swap(Section newSection) {
        if (newSection.distance >= this.distance) {
            throw new IllegalDistanceException();
        }
        this.distance -= newSection.distance;
        if (hasSameUpStation(newSection)) {
            this.upStation = newSection.downStation;
            return;
        }
        this.downStation = newSection.upStation;
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

    public int getDistance() {
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
        return Objects.equals(upStation, section.upStation) && Objects
            .equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }
}
