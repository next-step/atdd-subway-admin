package nextstep.subway.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;

public class StationResponse {
    private Long id;
    private String name;

    public static StationResponse of(Station station) {
        if (station == null) {
            return null;
        }

        return new StationResponse(station.getId(), station.getName());
    }

    public StationResponse() {
        
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
