package nextstep.subway.dto.stations;

public class StationNameResponse {

	private Long id;

	private String name;

	private StationNameResponse() {
	}

	public StationNameResponse(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
