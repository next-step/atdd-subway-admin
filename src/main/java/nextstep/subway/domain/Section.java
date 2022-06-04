package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column
    private Long distance;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final Long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    public void updateLine(final Line line) {
        this.line = line;
    }

    public boolean isEqualToUpStation(final Station station) {
        return this.getUpStation().equals(station);
    }

    public boolean isDistanceEqualOrGreaterThan(final Section section) {
        if(this.getDistance() <= section.getDistance()) {
            return true;
        }
        return false;
    }

    public void updateUpStation(final Section addableSection) {
        distance = distance - addableSection.getDistance();
        this.upStation = addableSection.getDownStation();
    }

    public void updateDownStation(final Section addableSection) {
        distance = distance - addableSection.getDistance();
        this.downStation = addableSection.getUpStation();
    }
}
