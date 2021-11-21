package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

public class StationRequest {

    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toStation() {
        return Station.from(name);
    }

    public String getName() {
        return name;
    }
}
