package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;

public class SectionResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public SectionResponse(Long id, Station upStation, Station downStation, int distance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.upStation = StationResponse.of(upStation);
        this.downStation = StationResponse.of(downStation);
        this.distance = distance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public SectionResponse() {
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(), section.getDistance(), section.getCreatedDate(), section.getModifiedDate());
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
