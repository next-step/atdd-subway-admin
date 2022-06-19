package nextstep.subway.dto.station;

import nextstep.subway.domain.station.Station;

public class StationSimpleResponse {

    private Long id;
    private String name;

    private StationSimpleResponse() {
    }

    private StationSimpleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationSimpleResponse of(Station station) {
        return new StationSimpleResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
