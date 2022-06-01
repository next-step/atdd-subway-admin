package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void addLine(Line line) {
        this.line = line;
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

    public Integer getDistance() {
        return distance.getDistance();
    }

    public void updateUpSection(Section newSection) {
        distance.minus(newSection.getDistance());
        this.upStation = newSection.getDownStation();
    }

    public void updateDownSection(Section newSection) {
        distance.minus(newSection.getDistance());
        this.downStation = newSection.getUpStation();
    }
}
