package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @ManyToOne
    private Line line;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }
}
