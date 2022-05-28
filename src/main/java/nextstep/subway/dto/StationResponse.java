package nextstep.subway.dto;

import nextstep.subway.domain.Station;

public class StationResponse {
    private final Long id;
    private final String name;

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName());
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
