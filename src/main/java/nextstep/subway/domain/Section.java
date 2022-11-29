package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;
    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, Distance distance) {
        return new Section(line, upStation, downStation, distance);
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

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    public boolean isEqualsUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualsDownStation(Station station) {
        return downStation.equals(station);
    }

    public void updateUpStation(Section section) {
        this.distance = this.distance.subtract(section.distance);
        this.upStation = section.getUpStation();
    }

    public void updateDownStation(Section section) {
        this.distance = this.distance.subtract(section.distance);
        this.downStation = section.getDownStation();
    }

    public Distance addDistance(Section newSection) {
        return this.distance.add(newSection.distance);
    }

}
