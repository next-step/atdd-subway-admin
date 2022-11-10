package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;
	private LocalDateTime cratedDate;
	private LocalDateTime modifiedDate;

	private LineResponse() {
	}

	public LineResponse(Long id, String name, String color, List<StationResponse> stations,
						LocalDateTime cratedDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
		this.cratedDate = cratedDate;
		this.modifiedDate = modifiedDate;
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(),
			of(line.getUpStation(), line.getDownStation()),
			line.getCreatedDate(), line.getModifiedDate()
			);
	}

	public static List<StationResponse> of(Station...stations) {
		return Arrays.stream(stations)
				.map(StationResponse::of)
				.collect(Collectors.toList());
	}

	public static List<LineResponse> ofList(List<Line> lines) {
		return lines.stream().map(LineResponse::of).collect(Collectors.toList());
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

	public LocalDateTime getCratedDate() {
		return cratedDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
}
