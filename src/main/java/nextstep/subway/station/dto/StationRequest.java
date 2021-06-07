package nextstep.subway.station.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseDTO;
import nextstep.subway.station.domain.Station;

@Builder
@Getter @NoArgsConstructor
public class StationRequest extends BaseDTO<Station> {
    private String name;

    private StationRequest(final String name) {
        this.name = name;
    }

    @Override
    public Station toEntity() {
        return new Station(name);
    }
}
