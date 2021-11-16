package nextstep.subway.line.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
	private Station downStation;

	private int distance;

	public Section() {
	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line=line;
		this.upStation=upStation;
		this.downStation=downStation;
		this.distance=distance;
	}
}
