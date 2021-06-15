package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Table(name = "section")
@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    private int distance;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    protected Section() {
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(null, upStation, downStation, distance, null);
    }


    public Section(Long id, Station upStation, Station downStation, int distance, Line line) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
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
        return distance;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
