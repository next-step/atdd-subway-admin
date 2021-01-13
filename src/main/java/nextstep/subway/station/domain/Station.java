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
    private Line lineStation;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public void setLineStation(Line lineStation) {
        this.lineStation = lineStation;
        //메모리상에도 일치
        //lineStation.getStations().add(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
