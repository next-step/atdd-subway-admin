package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import nextstep.subway.domain.Station;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public class StationResponse {
    private Long id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    private LocalDateTime createdDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable
    private LocalDateTime modifiedDate;

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
                station.getModifiedDate());
    }

    public static StationResponse toLineResponse(Station station) {
        return new StationResponse(station.getId(), station.getName(), null, null);
    }

    public StationResponse() {
    }

    public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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
