package nextstep.subway.line.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station_id"))
    private Station upStation;

    @ManyToOne
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

    public void updateDownStation(Section section) {
        downStation = section.getUpStation();
        distance = distance.minus(section.distance);
    }

    public void updateUpStation(Section section) {
        upStation = section.getDownStation();
        distance = distance.minus(section.distance);
    }

    public void mergeDownStation(Section section) {
        downStation = section.downStation;
        distance = distance.plus(section.distance);
    }

    public void mergeUpStation(Section section) {
        upStation = section.upStation;
        distance = distance.plus(section.distance);
    }

    public boolean isEqualToDownStation(Station station) {
        return downStation.isEqualTo(station);
    }

    public boolean isEqualToUpStation(Station station) {
        return upStation.isEqualTo(station);
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

}
