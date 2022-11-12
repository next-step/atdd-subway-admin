package nextstep.subway.dto;

import nextstep.subway.domain.Section;

import java.time.LocalDateTime;

public class SectionResponse {
    private final long id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation().getId(), section.getDownStation().getId(),
                section.getDistance(), section.getCreatedDate(), section.getModifiedDate());
    }

    private SectionResponse(final Long id, final Long upStationId, final Long downStationId, final int distance,
                            final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
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
