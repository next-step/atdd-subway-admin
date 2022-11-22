package nextstep.subway.dto;

import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationName;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(new StationName(name));
    }
}
