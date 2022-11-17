package nextstep.subway.dto;

import nextstep.subway.domain.Name;
import nextstep.subway.domain.Station;

public class StationRequest {
    private Name name;

    public Name getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
