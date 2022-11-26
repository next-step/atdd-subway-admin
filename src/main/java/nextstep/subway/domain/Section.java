package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
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

    public Section(final Station upStation, final Station downStation, final Distance distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(final Long id, final Station upStation, final Station downStation, final Distance distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public boolean contains(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public boolean equalsUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean equalsDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void minusDistance(int distance) {
        this.distance = this.distance.subtract(distance);
    }

    public void updateUpStation(Station station) {
        this.upStation = station;
    }

    public void updateDownStation(Station station) {
        this.downStation = station;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
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
