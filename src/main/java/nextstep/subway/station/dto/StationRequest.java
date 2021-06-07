package nextstep.subway.station.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

@Getter @NoArgsConstructor
public class StationRequest {
    private String name;

    public Station toStation() {
        return new Station(name);
    }
}
