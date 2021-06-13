package nextstep.subway.station.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import nextstep.subway.station.domain.Station;

public class StationResponse {
	private Long id;
	private String name;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	protected StationResponse() {
	}

	public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
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
		return Objects.equals(id, that.id) && Objects.equals(name, that.name)
			&& Objects.equals(createdDate, that.createdDate) && Objects.equals(modifiedDate,
			that.modifiedDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, createdDate, modifiedDate);
	}
}
