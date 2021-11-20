package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {

	private Long id; // 구간ID
	private Station upStation;         // 상행역 아이디
	private Station downStation;       // 하행역 아이디
	private int distance;             // 거리

	private SectionResponse() {
	}

	public SectionResponse(Long id, Station upStation, Station downStation, int distance) {
		this.id = id;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static SectionResponse from(Section section) {
		return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(), section.getDistance());
	}

	public Long getId() {
		return id;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getDistance() {
		return distance;
	}
}
