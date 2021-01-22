package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public StationRequest() {
    }

    public Station toStation() {
        return new Station(name);
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public static StationRequest of(String name) {
        return new StationRequest(name);
    }
}
