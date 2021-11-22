package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long upStationId;
    @Column(nullable = false)
    private Long downStationId;
    @Column(nullable = false)
    private int distance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    public Section(Long upStationId, Long downStationId, int distance) {
        this(upStationId, downStationId, distance, null);
    }

    public Section(Long upStationId, Long downStationId, int distance, Line line) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.line = line;
    }
}
