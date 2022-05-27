package nextstep.subway.dto;

import nextstep.subway.domain.Station;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"" +
                '}';
    }
}
