package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse {
	private final Long id;
	private final String name;
	private final String color;
	private final List<StationResponse> stations;
	private final LocalDateTime createdDate;
	private final LocalDateTime modifiedDate;

	private LineResponse(Long id, String name, String color, List<Station> stations, LocalDateTime createdDate,
		LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations.stream().map(StationResponse::of).collect(Collectors.toList());
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStationsFromUpTerminal(),
			line.getCreatedDate(), line.getModifiedDate());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
}
