package nextstep.subway.line.dto;

import java.time.LocalDateTime;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {
	private Long id;
	private String name;
	private int distance;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public static SectionResponse of(Section section) {
		Station station = section.getStation();
		return new SectionResponse(station.getId(), station.getName(), section.getDistance(), station.getCreatedDate(), station.getModifiedDate());
	}

	public SectionResponse() {
	}

	public SectionResponse(Long id) {
		this.id = id;
	}

	public SectionResponse(Long id, String name, int distance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.distance = distance;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getDistance() {
		return distance;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
}
