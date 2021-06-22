package nextstep.subway.section.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.Objects;

import javax.persistence.Column;
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
    private Long id;

    @JoinColumn(name = "line_id")
    @ManyToOne(fetch = LAZY)
    private Line line;

    @Column(name = "line_id", insertable = false, updatable = false)
    private Long lineId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @Column(name = "up_station_id", insertable = false, updatable = false)
    private Long upStationId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(name = "down_station_id", insertable = false, updatable = false)
    private Long downStationId;

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
        return new Section(line, upStation, downStation, distance);
    }

    public void updateStation(Station upStation, Station downStation, Distance requestDistance) {
        if (isUpStation(upStation) || isDownStation(downStation)) {
            distance = distance.diff(requestDistance);
        }
        if (isUpStation(upStation)) {
            this.upStation = downStation;
        }
        if (isDownStation(downStation)) {
            this.downStation = upStation;
        }
    }

    public void updateSectionByDownStation(Section section) {
        downStation = section.getDownStation();
        this.distance = this.distance.add(section.distance);
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

    public boolean isUpStation(Station station) {
        return upStation == station;
    }

    public boolean isDownStation(Station station) {
        return downStation == station;
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
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
