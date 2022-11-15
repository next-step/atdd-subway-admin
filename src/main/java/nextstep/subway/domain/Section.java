package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_upstation"), nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_downstation"), nullable = false)
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Line line, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Line line, int distance) {
        return new Section(upStation, downStation, line, Distance.from(distance));
    }

    public void updateStation(Section section) {
        if (upStation.equals(section.upStation)) {
            updateUpStation(section);
        }
        if (downStation.equals(section.downStation)) {
            updateDownStation(section);
        }
    }

    private void updateUpStation(Section section) {
        upStation = section.downStation;
        distance = distance.substract(section.distance);
    }

    private void updateDownStation(Section section) {
        downStation = section.upStation;
        distance = distance.substract(section.distance);
    }

    public Long getId() {
        return id;
    }

    public Distance getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
