package nextstep.subway.station.dto;

import nextstep.subway.station.Station;

public class StationRequestDto {
    private String name;

    private StationRequestDto() {}

    public StationRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
