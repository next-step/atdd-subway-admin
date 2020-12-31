package nextstep.subway.section.dto;

import java.util.Objects;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {

	private Long id;
	private Long upStationId;
	private Long downStationId;
	private Integer distance;

	public SectionResponse() {
	}

	public SectionResponse(Long id, Station upStation, Station downStation, Integer distance) {
		this.id = id;
		this.upStationId = upStation.getId();
		this.downStationId = downStation.getId();
		this.distance = distance;
	}

	public SectionResponse(Long upStationId, Long downStationId, Integer distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(),
			  section.getDistance());
	}

	public Long getId() {
		return id;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Integer getDistance() {
		return distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SectionResponse that = (SectionResponse) o;
		return Objects.equals(id, that.id) && Objects
			  .equals(downStationId, that.downStationId) && Objects
			  .equals(upStationId, that.upStationId) && Objects
			  .equals(distance, that.distance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, downStationId, upStationId, distance);
	}
}
