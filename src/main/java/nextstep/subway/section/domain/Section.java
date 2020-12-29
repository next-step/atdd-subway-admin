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

    @ManyToOne
    @JoinColumn(name = "up")
    private Station up;

    @ManyToOne
    @JoinColumn(name = "down")
    private Station down;

    @ManyToOne
    @JoinColumn(name = "line")
    private Line line;

    private Integer distance;
    private Integer sequence;

    protected Section() {
    }

    public Section(Line line, Station up, Station down, int distance, Integer sequence) {
        super();
        this.line = line;
        this.up = up;
        this.down = down;
        this.distance = distance;
        this.sequence = sequence;
    }

    public Long getId() {
        return this.id;
    }

    public Integer getSequence() {
        return this.sequence;
    }

    public Station getUp() {
        return this.up;
    }

    public Station getDown() {
        return this.down;
    }
}
