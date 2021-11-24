package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lineId")
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "upStationId")
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "downStationId")
	private Station downStation;

	private int distance;

	private int sequence;

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getSequence() {
		return sequence;
	}

	protected Section() {
	}

	public static Section create(Line line, Station upStation, Station downStation, int distance) {
		Section section = new Section();
		section.setLine(line);
		section.upStation = upStation;
		section.downStation = downStation;
		section.distance = distance;
		return section;
	}

	private void setLine(Line line) {
		this.line = line;
		this.sequence = line.sectionsSize() + 1;
		line.addSection(this);
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return Objects.equals(id, section.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
