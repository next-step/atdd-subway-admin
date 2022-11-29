package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int distance;
    @OneToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @OneToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(int distance, Station upStation, Station downStation) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public void toLine(Line line) {
        this.line = line;
    }
}
