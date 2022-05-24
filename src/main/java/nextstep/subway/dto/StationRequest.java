package nextstep.subway.dto;

import nextstep.subway.domain.station.Station;

public class StationRequest {
    private String name;

    protected StationRequest() {}

    private StationRequest(String name) {
        this.name = name;
    }

    public static StationRequest from(String name) {
        return new StationRequest(name);
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
