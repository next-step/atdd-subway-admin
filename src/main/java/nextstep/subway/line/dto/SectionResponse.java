package nextstep.subway.line.dto;

import java.time.LocalDateTime;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionResponse {

	private Long id;

	private Line line;

	private Station up;

	private Station down;

	private LocalDateTime createdDate;

	private LocalDateTime modifiedDate;

	public SectionResponse(final Long id, final Line line, final Station up, final Station down,
		final LocalDateTime createdDate,
		final LocalDateTime modifiedDate) {
		this.id = id;
		this.line = line;
		this.up = up;
		this.down = down;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Station getUp() {
		return up;
	}

	public Station getDown() {
		return down;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public static SectionResponse of(final Section section) {
		return Builder.builder()
			.id(section.getId())
			.up(section.getUp())
			.down(section.getDown())
			.createdDate(section.getCreatedDate())
			.modifiedDate(section.getModifiedDate())
			.build();
	}

	public static final class Builder {
		private Long id;
		private Line line;
		private Station up;
		private Station down;
		private LocalDateTime createdDate;
		private LocalDateTime modifiedDate;

		private Builder() {
		}

		public static Builder builder() {
			return new Builder();
		}

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder line(Line line) {
			this.line = line;
			return this;
		}

		public Builder up(Station up) {
			this.up = up;
			return this;
		}

		public Builder down(Station down) {
			this.down = down;
			return this;
		}

		public Builder createdDate(LocalDateTime createdDate) {
			this.createdDate = createdDate;
			return this;
		}

		public Builder modifiedDate(LocalDateTime modifiedDate) {
			this.modifiedDate = modifiedDate;
			return this;
		}

		public SectionResponse build() {
			return new SectionResponse(id, line, up, down, createdDate, modifiedDate);
		}
	}
}
