package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;

public class StationResponse {

    private Long id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime modifiedDate;

    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate());
    }

    public static StationResponse of(Long id, String name) {
        return new StationResponse(id, name);
    }

    public StationResponse() {
    }


    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
