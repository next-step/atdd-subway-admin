package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station_id"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station_id"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void updateDownStationAndMinusDistance(Section section) {
        downStation = section.getUpStation();
        distance = distance.minus(section.distance);
    }

    public void updateUpStationAndMinusDistance(Section section) {
        upStation = section.getDownStation();
        distance = distance.minus(section.distance);
    }

    public void updateDownStationAndPlusDistance(Section section) {
        downStation = section.downStation;
        distance = distance.plus(section.distance);
    }

    public void updateUpStationAndPlusDistance(Section section) {
        upStation = section.upStation;
        distance = distance.plus(section.distance);
    }

    public boolean isEqualToDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isEqualToUpStation(Station station) {
        return upStation.equals(station);
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

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line,
            section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
            section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
