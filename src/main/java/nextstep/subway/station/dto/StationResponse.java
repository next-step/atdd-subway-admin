package nextstep.subway.station.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import nextstep.subway.station.domain.Station;

@Getter
public class StationResponse {
	private Long id;
	private String name;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public static StationResponse of(Station station) {
		return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
			station.getModifiedDate());
	}

	public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}
}
