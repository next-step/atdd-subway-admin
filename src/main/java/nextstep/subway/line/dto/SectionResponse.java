package nextstep.subway.line.dto;

import java.time.LocalDateTime;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {

	private Long id;
	private Line line;
	private Station downStation;
	private Station upStation;
	private Distance distance;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	private SectionResponse(final Long id, final Line line, final Station downStation, final Station upStation,
		final Distance distance,
		final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
		this.id = id;
		this.line = line;
		this.downStation = downStation;
		this.upStation = upStation;
		this.distance = distance;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public SectionResponse of(final Section section) {
		return new SectionResponse(section.getId(), section.getLine(), section.getDownStation(), section.getUpStation(),
			section.getDistance(), section.getCreatedDate(), section.getModifiedDate());
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Station getDownStation() {
		return downStation;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Distance getDistance() {
		return distance;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}
}
