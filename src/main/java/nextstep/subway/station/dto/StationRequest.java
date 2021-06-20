package nextstep.subway.station.dto;

import java.util.Objects;

import nextstep.subway.station.domain.Station;

public class StationRequest {

    private String name;

    private Long id;

    public StationRequest() {
    }

    public StationRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        if (Objects.nonNull(id)) {
            return new Station(id, name);
        }
        return new Station(name);
    }

    public static StationRequest of(long id, String name) {
        return new StationRequest(id, name);
    }

}
