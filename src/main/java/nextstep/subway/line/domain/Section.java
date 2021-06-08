package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long lineId;

	private Long upStationId;

	private Long downStationId;

	private int distance;

	protected Section() {
	}

	public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
		this.lineId = lineId;
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section that = (Section)o;
		return Objects.equals(id, that.id) &&
			lineId == that.lineId &&
			Objects.equals(upStationId, that.upStationId) &&
			Objects.equals(downStationId, that.downStationId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, lineId, upStationId, downStationId);
	}

}
