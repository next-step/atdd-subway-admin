package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations = new ArrayList<>();
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	protected LineResponse() {
	}

	public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate,
		List<StationResponse> stations) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		//this.stations = stationsToResponses(stations);
		this.stations = stations;
	}

	public static LineResponse of(Line line, List<StationResponse> stationResponses) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(),
			line.getModifiedDate(), stationResponses);
	}

	/*private List<StationResponse> stationsToResponses(List<Station> stations) {
		return stations.stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}*/

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(),
			line.getModifiedDate(), new ArrayList<>());
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
