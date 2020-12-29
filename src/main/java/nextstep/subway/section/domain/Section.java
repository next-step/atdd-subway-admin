package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.distance.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "departure_id")
    private Station departure;

    @ManyToOne
    @JoinColumn(name = "arrival_id")
    private Station arrival;

    @Embedded
    private Distance distance;

    public Section(final Line line, final Station departure, final Station arrival, final Distance distance) {
        this.line = line;
        this.departure = departure;
        this.arrival = arrival;
        this.distance = distance;
    }
}
