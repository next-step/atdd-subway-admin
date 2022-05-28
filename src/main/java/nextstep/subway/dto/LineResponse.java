package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public static LineResponse of(Line line) {
		return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getUpStation(),
				line.getDownStation(), line.getCreatedDate(), line.getModifiedDate());
	}

	public LineResponse() {
	}

	public LineResponse(Long id, String name, String color, Station upStation, Station downStation,
			LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		setStations(upStation, downStation);
	}
	
	private void setStations(Station... stations) {
		this.stations = new ArrayList<StationResponse>();

		for (Station station : stations) {
			this.stations.add(StationResponse.of(station));
		}
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
