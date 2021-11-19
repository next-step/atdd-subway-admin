package nextstep.subway.station.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_station_to_line"))
    private Line line;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(Long upStationId) {
        this.id = upStationId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
