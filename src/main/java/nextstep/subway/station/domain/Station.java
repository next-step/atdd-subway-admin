package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    // todo: 환승역 요구사항 있을 경우 Station:Line=다대다 로 변경
    @ManyToOne
    private Line line;

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

    public void setLine(Line line) {
        this.line = line;
    }
}
