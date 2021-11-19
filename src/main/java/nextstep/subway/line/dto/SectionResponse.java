package nextstep.subway.line.dto;

import java.time.LocalDateTime;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {
	private final Long id;
	private final StationResponse upStation;
	private final StationResponse downStation;
	private final int distance;
	private final LocalDateTime createdDate;
	private final LocalDateTime modifiedDate;

	private SectionResponse(Long id, Station upStation, Station downStation, int distance,
		LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.upStation = StationResponse.of(upStation);
		this.downStation = StationResponse.of(downStation);
		this.distance = distance;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(),
			section.getDistance(), section.getCreatedDate(), section.getModifiedDate());
	}

	public Long getId() {
		return id;
	}

	public StationResponse getUpStation() {
		return upStation;
	}

	public StationResponse getDownStation() {
		return downStation;
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
