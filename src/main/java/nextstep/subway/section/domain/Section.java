package nextstep.subway.section.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
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

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Section getInstance(Line line, Station upStation, Station downStation, Distance distance) {
        Section section = new Section(line, upStation, downStation, distance);
        line.addSection(section);
        return section;
    }

    public List<Station> getUpDownStations() {
        return new ArrayList<>(Arrays.asList(upStation, downStation));
    }

    public void updateStation(Station upStation, Station downStation, Distance distance) {
        if (isUpStation(upStation)) {
            this.upStation = downStation;
        }
        if (isDownStation(downStation)) {
            this.downStation = upStation;
        }
        this.distance = calculateDistance(distance);
    }

    public boolean isContain(Station station) {
        return isUpStation(station) || isDownStation(station);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    private boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    private boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    private Distance calculateDistance(Distance requestDistance) {
        return calculate(requestDistance);
    }

    private Distance calculate(Distance requestDistance) {
        return new Distance(distance.getValue() - requestDistance.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
