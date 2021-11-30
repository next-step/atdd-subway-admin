package nextstep.subway.station.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_station_to_line"))
    private Line line;

    protected Station() {
    }

    public Station(final String name) {
        this.name = name;
    }

    public Station(final Long id, final String name, final Line line) {
        this.id = id;
        this.name = name;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
