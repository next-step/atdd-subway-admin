package nextstep.subway.line.dto;

import nextstep.subway.line.domain.SafeStationInfo;

import java.time.LocalDateTime;

public class StationInLineResponse {
    private final Long id;
    private final String name;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    StationInLineResponse(
            final Long id, final String name, final LocalDateTime createdDate, final LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationInLineResponse of(SafeStationInfo safeStationInfo) {
        return new StationInLineResponse(
                safeStationInfo.getId(),
                safeStationInfo.getName(),
                safeStationInfo.getCreatedDate(),
                safeStationInfo.getModifiedDate()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
