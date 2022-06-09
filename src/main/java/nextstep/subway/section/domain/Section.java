package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

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

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
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
        return distance.getDistance();
    }

    public Line getLine() {
        return line;
    }

    public void updateLine(Line line) {
        this.line = line;
    }

    public boolean matchAllStations(Section section) {
        return this.upStation.equals(section.upStation) &&
                this.downStation.equals(section.downStation);
    }

    public boolean hasUpStations(Section section) {
        return this.upStation.equals(section.upStation) ||
                this.upStation.equals(section.downStation);
    }

    public boolean hasDownStations(Section section) {
        return this.downStation.equals(section.upStation) ||
                this.downStation.equals(section.downStation);
    }

    public boolean hasStation(Long stationId) {
        return isUpStationId(stationId) || isDownStationId(stationId);
    }

    private boolean isUpStationId(Long stationId) {
        return this.upStation.getId() == stationId;
    }

    private boolean isDownStationId(Long stationId) {
        return this.downStation.getId() == stationId;
    }

    public void updateDistance(Section section) {
        this.distance = this.distance.subtract(section.getDistance());
    }

    public boolean isSameUpStation(Section section) {
        return this.upStation.equals(section.getUpStation());
    }

    public void updateUpStation(Section section) {
        this.upStation = section.getDownStation();
    }

    public boolean isSameDownStation(Section section) {
        return this.downStation.equals(section.getDownStation());
    }

    public void updateDownStation(Section section) {
        this.downStation = section.getUpStation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, line, distance);
    }

    public boolean hasUpStation(Long stationId) {
        return this.upStation.getId() == stationId;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }
}
