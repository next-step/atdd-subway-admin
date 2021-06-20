package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "station")
    private List<LineStation> lineStations = new ArrayList<>();

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
