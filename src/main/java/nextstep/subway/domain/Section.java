package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.exception.StationNotFoundException;

@Entity
public class Section extends BaseEntity {
	private static final int MIN_DISTANCE = 0;
	private static final int INIT_SECTION_ORDER = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id", nullable = false)
	private Station upStation;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id", nullable = false)
	private Station downStation;

	private int distance;

	private int sectionOrder;

	protected Section() {
	}

	public Section(Station upStation, Station downStation, int distance) {
		validation(upStation, downStation, distance);

		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
		this.sectionOrder = INIT_SECTION_ORDER;
	}

	private void validation(Station upStation, Station downStation, int distance) {
		if (Objects.isNull(upStation) || Objects.isNull(downStation) 
				|| upStation.getId() == null || downStation.getId() == null) {
			throw new StationNotFoundException("역 정보가 없습니다.");
		}

		if (upStation.equals(downStation)) {
			throw new IllegalArgumentException("같은 역이 입력되었습니다.");
		}

		if (distance <= MIN_DISTANCE) {
			throw new IllegalArgumentException("길이는 0이상이여야 합니다.");
		}
	}

	protected boolean exists(Station station) {
		return upStation.equals(station) || downStation.equals(station);
	}

	protected boolean upStationEquals(Station station) {
		return upStation.equals(station);
	}

	protected boolean downStationEquals(Station station) {
		return downStation.equals(station);
	}

	/*
	 * Title : Sectinos 관리 Content : Sections에서 값을 추가하기전 비교하기 위해 사용하는 함수들
	 */

	public void addUpStation(Section section) {
		validationDistance(section.getDistance());
		this.distance -= section.distance;
		this.upStation = section.downStation;
	}

	public void addDownStation(Section section) {
		validationDistance(section.getDistance());
		this.distance -= section.distance;
		this.downStation = section.upStation;
	}

	private void validationDistance(int distance) {
		if (this.distance <= distance) {
			throw new IllegalArgumentException(this.distance + "보다 큰 길이가 입력되어야합니다.");
		}
	}

	protected void updateSectionOrder(int index) {
		this.sectionOrder = index;
	}

	protected void orderIncrease() {
		this.sectionOrder++;
	}
	/* Sections 관리 End */

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

	public int getSectionOrder() {
		return sectionOrder;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Section other = (Section) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Section [id=" + id + ", upStation=" + upStation + ", downStation=" + downStation + ", distance="
				+ distance + ", sectionOrder=" + sectionOrder + "]";
	}
}
