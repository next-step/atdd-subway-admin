package nextstep.subway.dto;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;

public class SectionResponse {
    private final long id;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(),
                section.getDistance(), section.getCreatedDate(), section.getModifiedDate());
    }

    private SectionResponse(final Long id, final Station upStation, final Station downStation, final Distance distance,
                           final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.upStationId = upStation.getId();
        this.downStationId = downStation.getId();
        this.distance = distance.getDistance();
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
