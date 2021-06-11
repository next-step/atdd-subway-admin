package nextstep.subway.line.section;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Line upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Line downStation;

	@Embedded
	private Distance distance;
}
