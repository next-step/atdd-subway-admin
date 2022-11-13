package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

	private Long id;
	private String name;
	private String color;
	private List<LineStationResponse> sections;
	private LocalDateTime cratedDate;
	private LocalDateTime modifiedDate;

	private LineResponse() {
	}

	public LineResponse(Long id, String name, String color, List<LineStationResponse> sections,
						LocalDateTime cratedDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.sections = sections;
		this.cratedDate = cratedDate;
		this.modifiedDate = modifiedDate;
	}

	public static List<LineResponse> ofList(List<Line> lines) {
		return lines.stream().map(LineResponse::of).collect(Collectors.toList());
	}

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(),
				of(line.getLineStations()),
				line.getCreatedDate(), line.getModifiedDate()
			);
	}

	public static List<LineStationResponse> of(Sections sections) {
		return sections.stream()
				.map(LineStationResponse::of)
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

	public List<LineStationResponse> getSections() {
		return sections;
	}

	public LocalDateTime getCratedDate() {
		return cratedDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
}
