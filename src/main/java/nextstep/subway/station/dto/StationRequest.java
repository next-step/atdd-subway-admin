package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

public class StationRequest {

    public StationRequest() {

    }

    public StationRequest(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
