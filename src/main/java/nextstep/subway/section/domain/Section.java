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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private int distance;

    protected Section() {
    }

    public Section(Station station, int distance) {
        this.station = station;
        this.distance = distance;
    }

    public static Section first(Station firstStop) {
        return new Section(firstStop, FIRST_STOP_DISTANCE);
    }

    public void toLine(Line line) {
        this.line = line;
        if (!line.contains(this)) {
            line.add(this);
        }
    }

    public Station getStation() {
        return station;
    }
}
