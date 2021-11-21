package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<Station> stations = new ArrayList<>();
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	private LineResponse() {
	}

	private LineResponse(Long id, String name, String color, List<Station> stations, LocalDateTime createdDate,
		LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static LineResponse from(Line line) {
		List<Station> stations = converToStations(line.getOrderedSections());
		return new LineResponse(line.getId(), line.getName(), line.getColor(), stations,
			line.getCreatedDate(), line.getModifiedDate());
	}

	private static List<Station> converToStations(List<Section> orderedSections) {
		List<Station> stations = orderedSections.stream().map(Section::getUpStation).collect(Collectors.toList());
		stations.add(orderedSections.get(orderedSections.size() - 1).getDownStation());
		return stations;
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

	public List<Station> getStations() {
		return stations;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
}
