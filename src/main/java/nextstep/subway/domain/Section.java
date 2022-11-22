package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Long distance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {

    }

    public Section(Station upStation, Station downStation, long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void belongLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return this.line;
    }
}
