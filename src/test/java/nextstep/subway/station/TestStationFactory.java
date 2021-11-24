package nextstep.subway.station;

import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.TestPostRequestFactory;

public class TestStationFactory {

    public static StationResponse 역_생성(String name) {
        StationRequest stationRequest = new StationRequest(name);

        return TestPostRequestFactory.요청_post("/stations", stationRequest)
                .body()
                .as(StationResponse.class);
    }
}
