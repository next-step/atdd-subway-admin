package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne
    @JoinColumn(name = "upStation_id", foreignKey = @ForeignKey(name = "fk_section_to_station_1"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downStation_id", foreignKey = @ForeignKey(name = "fk_section_to_station_2"))
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasUpStation(Station upStation) {
        return upStation.equals(this.upStation);
    }

    public boolean hasDownStation(Station downStation) {
        return downStation.equals(this.downStation);
    }

    public Long getLineId() {
        return line.getId();
    }

    public Long getUpStationId() {
        if (upStation == null) {
            return null;
        }
        return upStation.getId();
    }

    public Long getDownStationId() {
        if (downStation == null) {
            return null;
        }
        return downStation.getId();
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

    public int getDistance() {
        return distance;
    }

    public void switchUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void switchDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void updateDistance(int distance) {
        this.distance = distance;
    }
}
