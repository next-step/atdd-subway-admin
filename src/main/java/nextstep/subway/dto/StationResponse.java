package nextstep.subway.dto;

import java.time.LocalDateTime;
import nextstep.subway.domain.Station;

public class StationResponse extends BaseDto {
    private final Long id;
    private final String name;

    private StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        super(createdDate, modifiedDate);
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(),
                station.getModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
