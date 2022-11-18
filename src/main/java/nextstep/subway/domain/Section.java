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

    public boolean prevSection(Section newSection) {
        return this.upStation == newSection.upStation;
    }

    public void change(Section newSection) {
        if (newSection.distance >= this.distance) {
            throw new IllegalDistanceException();
        }
        if (hasSameStation(newSection)) {
            throw new SameStationException();
        }
        this.distance -= newSection.distance;
        this.upStation = newSection.downStation;
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
