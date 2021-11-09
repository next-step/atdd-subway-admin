package nextstep.subway.station.dto;

import javax.validation.constraints.NotBlank;
import nextstep.subway.common.domain.Name;
import nextstep.subway.station.domain.Station;

public class StationRequest {

    @NotBlank(message = "역 이름은 비어있을 수 없습니다.")
    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return Station.from(Name.from(name));
    }
}
