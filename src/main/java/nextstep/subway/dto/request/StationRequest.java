package nextstep.subway.dto.request;

import nextstep.subway.domain.Station;

public class StationRequest {

    private Long id;
    private String name;

    public Station toStation() {
        return new Station(name);
    }
    public String getName() {
        return name;
    }
}
