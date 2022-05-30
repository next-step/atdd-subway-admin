package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;

	public static LineResponse of(Line line) {
		return new LineResponse(line);
	}

	public LineResponse() {
	}

	public LineResponse(Line line) {
		this.id = line.getId();
		this.name = line.getName();
		this.color = line.getColor();
		setStations(line.getSections());
	}
	
	private void setStations(Sections sections) {
		stations = new ArrayList<StationResponse>();

		for (Section section: sections.getSections()) {
			this.stations.add(StationResponse.of(section.getUpStation()));
		}
		this.stations.add(
				StationResponse.of(sections
						.lastSection()
						.getDownStation()));
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
}
