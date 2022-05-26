package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    private int distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public void setLine(Line line) {
        this.line = line;
    }

    public void update(Section newSection) {
        if (this.upStation.equals(newSection.upStation)) {
            updateUpStation(newSection);
        }
        if (this.downStation.equals(newSection.downStation)) {
            updateDownStation(newSection);
        }
    }

    private void updateUpStation(Section section) {
        this.upStation = section.downStation;
        updateDistance(section.distance);
    }

    private void updateDownStation(Section section) {
        this.downStation = section.upStation;
        updateDistance(section.distance);
    }

    private void updateDistance(int newDistance) {
        int updateDistance = this.distance - newDistance;
        if (updateDistance <= 0) {
            throw new IllegalArgumentException("invalid distance.");
        }
        this.distance = updateDistance;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }


    public boolean isSame(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }

    public boolean isAfter(Section section) {
        return this.upStation.equals(section.downStation);
    }

    public boolean isBefore(Section section) {
        return this.downStation.equals(section.upStation);
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public Section combine(Section afterSection) {
        Section newSection = new Section(this.upStation, afterSection.getDownStation(), this.distance + afterSection.distance);
        newSection.setLine(this.line);
        return newSection;
    }

}
