package nextstep.subway.dto;

import nextstep.subway.domain.Station;

public class LineInStationResponse {
    private final Long id;
    private final String name;

    public LineInStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static LineInStationResponse from(Station station) {
        return new LineInStationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}