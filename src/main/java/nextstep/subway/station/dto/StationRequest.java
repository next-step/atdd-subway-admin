package nextstep.subway.station.dto;

import nextstep.subway.domain.Station;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return Station.create(name);
    }
}
