package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private List<StationResponse> stations;

	private LineResponse() {
	}

	public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate,
		List<StationResponse> stations) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.stations = stations;
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(),
			line.getModifiedDate(), getStationResponses(line));
	}

	private static List<StationResponse> getStationResponses(Line line) {
		return line.getStations().stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public List<StationResponse> getStations() {
		return stations;
	}
}
