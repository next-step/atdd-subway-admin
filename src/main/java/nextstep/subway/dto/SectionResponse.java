package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {

	private StationResponse upStation;
	private StationResponse downStation;
	private Integer distance;

	private SectionResponse() {
	}

	public SectionResponse(StationResponse upStation, StationResponse downStation, Integer distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}


	public StationResponse getUpStation() {
		return upStation;
	}

	public StationResponse getDownStation() {
		return downStation;
	}

	public Integer getDistance() {
		return distance;
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(
				StationResponse.of(section.getUpStation()),
				StationResponse.of(section.getDownStation()),
				section.getDistance().getValue());
	}
}
