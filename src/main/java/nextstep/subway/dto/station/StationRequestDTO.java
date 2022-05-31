package nextstep.subway.dto.station;

import nextstep.subway.domain.station.Station;

public class StationRequestDTO {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
