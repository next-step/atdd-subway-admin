package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;

/**
 * @author : byungkyu
 * @date : 2021/01/04
 * @description :
 **/
@Entity
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;
	private Long upStationId;
	private Long downStationId;
	private int distance;


	public Section(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public List<Long> getStationsIds() {
		return Arrays.asList(upStationId, downStationId);
	}

	public void changeUpStation(Long id, int distance) {
		this.upStationId = id;
		this.distance = distance;
	}

	public void changeDownStation(Long id, int distance) {
		this.downStationId = id;
		this.distance = distance;
	}

	public void changeUpStationWithDistance(Section originSection, Section section) {
		this.upStationId = originSection.getDownStationId();
		this.distance = originSection.getDistance() - section.getDistance();
	}

	public boolean isDistanceGreaterThan(int distance) {
		return this.distance > distance;
	}


	public boolean isInValidDistance(int distance) {
		return (this.getDistance() == distance || this.getDistance() < distance);
	}

	public boolean isDuplicateAllStation(Long upStationId, Long downStationId) {
		return (this.upStationId == upStationId && this.downStationId == downStationId);
	}

	public boolean isNotExistAllStation(Long upStationId, Long downStationId) {
		return (!getStationsIds().contains(upStationId) && !getStationsIds().contains(downStationId));
	}

	protected Section() {
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return getDistance() == section.getDistance() && Objects.equals(getId(), section.getId())
			&& Objects.equals(getLine(), section.getLine()) && Objects.equals(getUpStationId(),
			section.getUpStationId()) && Objects.equals(getDownStationId(), section.getDownStationId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getLine(), getUpStationId(), getDownStationId(), getDistance());
	}

}
