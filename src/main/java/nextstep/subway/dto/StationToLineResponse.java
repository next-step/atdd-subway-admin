package nextstep.subway.dto;

import nextstep.subway.domain.Station;

public class StationToLineResponse {

    private Long id;
    private String name;

    public StationToLineResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StationToLineResponse fromStation(Station station) {
        return new StationToLineResponse(station.getId(), station.getName());
    }
}
