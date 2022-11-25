package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    protected Section() {
    }

    public Section(Line line, Long distance, Station upStation, Station downStation) {
        this.line = line;
        this.distance = Distance.from(distance);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Section)) {
            return false;
        }
        Section section = (Section) object;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, distance, upStation, downStation);
    }

    public void rebase(Section section) {
        rebaseIfUpStationEquals(section);
        rebaseIfDownStationEquals(section);
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

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public boolean containsStation(Station station) {
        return getStations().contains(station);
    }

    public void mergeUpStation(Section upSection) {
        this.upStation = upSection.getUpStation();
        this.distance = distance.add(upSection.distance);
    }

    private void rebaseIfUpStationEquals(Section section) {
        if(this.upStation.equals(section.upStation)) {
            distance = distance.subtract(section.distance);
            upStation = section.downStation;
        }
    }

    private void rebaseIfDownStationEquals(Section section) {
        if(this.downStation.equals(section.downStation)) {
            distance = distance.subtract(section.distance);
            downStation = section.upStation;
        }
    }
}
