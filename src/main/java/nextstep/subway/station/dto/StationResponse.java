package nextstep.subway.station.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import nextstep.subway.common.dto.BaseResponse;
import nextstep.subway.station.domain.Station;

public class StationResponse extends BaseResponse {
	private Long id;
	private String name;

	protected StationResponse() {
		super();
	}

	public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		super(createdDate,modifiedDate);
		this.id = id;
		this.name = name;
	}

	public static StationResponse of(Station station) {
		return new StationResponse(station.id(), station.name(), station.createdDate(),
			station.modifiedDate());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof StationResponse)) {
			return false;
		}
		StationResponse that = (StationResponse)object;
		return Objects.equals(id, that.id)
			&& Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
