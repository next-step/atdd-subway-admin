package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {
	private Long id;
	private String name;
	private int distance;

	public SectionResponse() {

	}

	public SectionResponse(Long id, String name, int distance) {
		this.id = id;
		this.name = name;
		this.distance = distance;
	}

	public static SectionResponse of(Section section) {
		Station station = section.getDownStation();
		return new SectionResponse(station.getId(), station.getName(), section.getDistance());
	}

	public Long getId() {
		return id;
	}
}
