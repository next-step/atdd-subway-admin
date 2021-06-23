package nextstep.subway.section.domain;

import java.util.List;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {

	private final Long id;

	private final int distance;

	private final LineResponse line;

	private final StationResponse upStation;

	private final StationResponse downStation;

	private SectionResponse(Section section) {
		this.id = section.getId();
		this.distance = section.getDistance();
		List<Station> stations = section.stations();
		this.line = LineResponse.of(section.getLine());
		this.upStation = StationResponse.of(stations.get(0));
		this.downStation = StationResponse.of(stations.get(1));
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section);
	}

	public Long getId() {
		return id;
	}

	public int getDistance() {
		return distance;
	}

	public LineResponse getLine() {
		return line;
	}

	public StationResponse getUpStation() {
		return upStation;
	}

	public StationResponse getDownStation() {
		return downStation;
	}
}
