package nextstep.subway.station.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseDTO;
import nextstep.subway.station.domain.Station;
import javax.validation.constraints.NotBlank;

@Getter @NoArgsConstructor
public class StationRequest extends BaseDTO<Station> {

    @NotBlank
    private String name;

    @Builder
    public StationRequest(final String name) {
        this.name = name;
    }

    @Override
    public Station toEntity() {
        return new Station(name);
    }
}
