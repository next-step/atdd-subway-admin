package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

public class StationRequest {
    private String name;

    protected StationRequest() {
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
