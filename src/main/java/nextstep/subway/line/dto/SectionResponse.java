package nextstep.subway.line.dto;

import java.time.LocalDateTime;

import nextstep.subway.line.domain.Section;

public class SectionResponse {

	private Long id;

	private Long lineId;

	private Long upStationId;

	private Long downStationId;

	private Integer distance;

	private LocalDateTime createdDate;

	private LocalDateTime modifiedDate;

	public SectionResponse(final Long id, final Long lineId, final Long upStationId, final Long downStationId,
		final Integer distance, final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
		this.id = id;
		this.lineId = lineId;
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Long getId() {
		return id;
	}

	public Long getLineId() {
		return lineId;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public Integer getDistance() {
		return distance;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public static SectionResponse of(final Section section) {
		return Builder.SectionResponse()
			.id(section.getId())
			.lineId(section.getLine().getId())
			.upStationId(section.getUp().getId())
			.downStationId(section.getDown().getId())
			.distance(section.getDistance().getDistance())
			.createdDate(section.getCreatedDate())
			.modifiedDate(section.getModifiedDate())
			.build();
	}

	public static final class Builder {
		private Long id;
		private Long lineId;
		private Long upStationId;
		private Long downStationId;
		private Integer distance;
		private LocalDateTime createdDate;
		private LocalDateTime modifiedDate;

		private Builder() {
		}

		public static Builder SectionResponse() {
			return new Builder();
		}

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder lineId(Long lineId) {
			this.lineId = lineId;
			return this;
		}

		public Builder upStationId(Long upStationId) {
			this.upStationId = upStationId;
			return this;
		}

		public Builder downStationId(Long downStationId) {
			this.downStationId = downStationId;
			return this;
		}

		public Builder distance(Integer distance) {
			this.distance = distance;
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
			return new SectionResponse(id, lineId, upStationId, downStationId, distance, createdDate, modifiedDate);
		}
	}

}
