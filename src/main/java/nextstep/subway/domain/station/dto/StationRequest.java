package nextstep.subway.domain.station.dto;

import nextstep.subway.domain.station.domain.Station;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
