package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStation_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStation_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
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

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void updateIfAdjacentSection(Section newSection) {
        if (upStation.equals(newSection.getUpStation())) {
            upStation = newSection.getDownStation();
            distance.subtract(newSection.getDistance());
        }
        if (downStation.equals(newSection.getDownStation())) {
            downStation = newSection.getUpStation();
            distance.subtract(newSection.getDistance());
        }
    }
}
