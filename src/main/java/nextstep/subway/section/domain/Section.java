package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    private static final int FIRST_STOP_DISTANCE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private Line line;

    @OneToOne
    @JoinColumn(name = "id")
    private Station station;

    private int distance;

    protected Section() {
    }

    public Section(Line line, Station station, int distance) {
        this.line = line;
        this.station = station;
        this.distance = distance;
    }

    public static Section firstStop(Line line, Station station) {
        return new Section(line, station, FIRST_STOP_DISTANCE);
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

    public int getDistance() {
        return distance;
    }
}
