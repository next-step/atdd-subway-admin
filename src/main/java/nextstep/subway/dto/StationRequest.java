package nextstep.subway.dto;

import nextstep.subway.domain.Station;

public class StationRequest {
    private String name;

    protected StationRequest() {}

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
