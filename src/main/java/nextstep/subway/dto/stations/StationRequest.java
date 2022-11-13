package nextstep.subway.dto.stations;

import nextstep.subway.domain.station.Station;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
