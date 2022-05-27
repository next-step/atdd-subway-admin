package nextstep.subway.dto;

import nextstep.subway.domain.Station;

public class StationRequest {

    private Long id;
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }

}
