package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

public class SectionResponse {

    private Long id;
    private String name;
    private int distance;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    public SectionResponse() {

    }

    public SectionResponse(Long id, String name, int distance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.createDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static SectionResponse of(Section section) {
        Station station = section.getStation();
        return new SectionResponse(station.getId(), station.getName(), section.getDistance(), station.getCreatedDate(), station.getModifiedDate());
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

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
