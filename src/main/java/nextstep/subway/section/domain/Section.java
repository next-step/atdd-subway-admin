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

    @ManyToOne(fetch = FetchType.EAGER)
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.EAGER)
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

    public void updateSection(Section section) {
        Station beforeDownStation = this.downStation;
        int beforeDistance = this.distance;

        this.downStation = section.downStation;
        this.distance = section.distance;

        section.upStation = section.downStation;
        section.downStation = beforeDownStation;
        section.distance = beforeDistance - this.distance;
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

    public boolean isSameUpStation(Section section) {
        return section.isSameUpStation(this.upStation);
    }

    private boolean isSameUpStation(Station upStation) {
        return Objects.equals(this.upStation, upStation);
    }
}
