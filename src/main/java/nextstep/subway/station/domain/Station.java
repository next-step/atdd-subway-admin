package nextstep.subway.station.domain;

import static javax.persistence.GenerationType.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicUpdate;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;

@Entity
@DynamicUpdate
public class Station extends BaseEntity implements Comparable<Station> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_station_to_line"))
    private Line line;

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    protected Station() {
    }

    public String getName() {
        return name;
    }

    public void setLine(Line saveLine) {
        this.line = saveLine;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int compareTo(Station o) {
        if (this.id > o.id) {
            return 1;
        }
        if (this.id < o.id) {
            return -1;
        }
        return 0;
    }
}
