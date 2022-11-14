package nextstep.subway.dto.line;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.domain.line.Line;
import nextstep.subway.dto.stations.StationNameResponse;

public class LineResponse {

	private Long id;
	private String name;
	private String color;
	private List<StationNameResponse> stations;

	private LineResponse() {
	}

	private LineResponse(Long id, String name, String color, List<StationNameResponse> stations) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
	}

	public static LineResponse of(Long id, String name, String color, List<StationNameResponse> stations) {
		return new LineResponse(id, name, color, stations);
	}

	public static LineResponse of(Line line) {
		List<StationNameResponse> stations = new ArrayList<>();
		stations.add(StationNameResponse.of(line.getUpStation()));
		stations.add(StationNameResponse.of(line.getDownStation()));
		return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
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

	public List<StationNameResponse> getStations() {
		return stations;
	}
}
