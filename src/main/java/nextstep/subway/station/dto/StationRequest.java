package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

public class StationRequest {
    private String name;

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public StationRequest() { }
}
