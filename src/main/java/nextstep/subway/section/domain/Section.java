package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance, line);
    }

    public void updateUpSection(Section section) {
        this.upStation = section.downStation;
        this.distance -= section.distance;
    }

    public void updateDownSection(Section section) {
        this.downStation = section.upStation;
        this.distance -= section.distance;
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
}
