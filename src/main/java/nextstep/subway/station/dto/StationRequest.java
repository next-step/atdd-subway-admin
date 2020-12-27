package nextstep.subway.station.dto;

import javax.validation.constraints.NotBlank;
import nextstep.subway.station.domain.Station;

public class StationRequest {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
