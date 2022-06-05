package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

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

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.from(distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void divideWith(Section newSection) {
        if (isSameUpStation(newSection.getUpStation())) {
            upStation = newSection.getDownStation();
            distance.subtract(newSection.getDistance());
            return;
        }
        if (isSameDownStation(newSection.getDownStation())) {
            downStation = newSection.getUpStation();
            distance.subtract(newSection.getDistance());
        }
    }

    public void mergeWith(Section nextSection) {
        downStation = nextSection.downStation;
        distance.add(nextSection.getDistance());
    }

    public boolean isSameUpStation(Station station){
        return upStation.equals(station);
    }

    public boolean isSameDownStation(Station station){
        return downStation.equals(station);
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

    public void updateLine(Line line) {
        this.line = line;
    }
}
