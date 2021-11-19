package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

public class StationRequest {
    private String name;

    public StationRequest() { // for json deserialization
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toStation() {
        return Station.of(name);
    }

    public String getName() {
        return name;
    }
}
