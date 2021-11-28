package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int distance;
    @ManyToOne
    @JoinColumn(name="STATION_ID")
    private Long upStationId;
    @ManyToOne
    @JoinColumn(name="STATION_ID")
    private Long downStationId;
    @ManyToOne
    private Line line;

    protected Section() {
    }
}
