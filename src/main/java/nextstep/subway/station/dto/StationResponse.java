package nextstep.subway.station.dto;

import java.time.LocalDateTime;

import nextstep.subway.station.domain.Station;

public class StationResponse {
	private final Long id;
	private final String name;
	private final LocalDateTime createdDate;
	private final LocalDateTime modifiedDate;

	private StationResponse(final Long id, final String name, final LocalDateTime createdDate,
		final LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static StationResponse of(Station station) {
		return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
			station.getModifiedDate());
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
}
