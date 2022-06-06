package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "upStationId", foreignKey = @ForeignKey(name = "fk_up_station"))
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "downStationId", foreignKey = @ForeignKey(name = "fk_down_station"))
	private Station downStation;

	private int distance;

	protected Section() {
	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public Long getId() {
		return id;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getDistance() {
		return distance;
	}

	public boolean isUpStation(Station station) {
		return this.upStation.equals(station);
	}
}
