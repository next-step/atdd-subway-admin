package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    private int distance;

    public Section() {

    }

    public Section(Long id, Line line, Station station, int distance) {
        this(line, station, distance);
        this.id = id;
    }

    public Section(Line line, Station station, int distance) {
        this.line = line;
        this.station = station;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Station getStation() {
        return station;
    }

    public Line getLine() {
        return line;
    }

    public Long getId() {
        return id;
    }
}
