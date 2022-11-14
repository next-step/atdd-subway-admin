package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

	private Long id;
	private String name;
	private String color;
	private List<String> stations;
	private LocalDateTime cratedDate;
	private LocalDateTime modifiedDate;

	private LineResponse() {
	}

	public LineResponse(Long id, String name, String color, List<String> stations,
						LocalDateTime cratedDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
		this.cratedDate = cratedDate;
		this.modifiedDate = modifiedDate;
	}

	public static List<LineResponse> ofList(List<Line> lines) {
		return lines.stream().map(LineResponse::of).collect(Collectors.toList());
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(),
				toStationName(line.getLineStations()),
				line.getCreatedDate(), line.getModifiedDate()
			);
	}

	public static List<String> toStationName(Sections sections) {
		return sections.getStations()
				.stream()
				.map(Station::getName)
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

	public List<String> getStations() {
		return stations;
	}

	public LocalDateTime getCratedDate() {
		return cratedDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
}
