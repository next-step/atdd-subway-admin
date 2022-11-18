package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import nextstep.subway.exception.IllegalDistanceException;
import nextstep.subway.exception.SameStationException;
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
        return this.upStation == newSection.upStation
            || this.upStation == newSection.downStation
            || this.downStation == newSection.upStation
            || this.downStation == newSection.downStation;
    }

    public boolean prevSection(Section newSection) {
        return this.upStation == newSection.upStation;
    }

    public boolean afterSection(Section newSection) {
        return this.downStation == newSection.downStation;
    }

    public void betweenBefore(Section newSection) {
        if (newSection.distance >= this.distance) {
            throw new IllegalDistanceException();
        }
        if (hasSameStation(newSection)) {
            throw new SameStationException();
        }
        this.distance -= newSection.distance;
        this.upStation = newSection.downStation;
    }

    public void betweenAfter(Section newSection) {
        if (newSection.distance >= this.distance) {
            throw new IllegalDistanceException();
        }
        if (hasSameStation(newSection)) {
            throw new SameStationException();
        }
        this.distance -= newSection.distance;
        this.downStation = newSection.upStation;
    }

    private boolean hasSameStation(Section newSection) {
        return this.upStation.equals(newSection.upStation) && this.downStation
            .equals(newSection.downStation);
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

}
