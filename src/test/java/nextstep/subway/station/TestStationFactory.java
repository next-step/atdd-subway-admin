package nextstep.subway.station;

import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpMethod;

import static nextstep.subway.utils.TestRequestFactory.요청;

public class TestStationFactory {

    public static StationResponse 역_생성(String name) {
        StationRequest stationRequest = new StationRequest(name);

        return 요청(HttpMethod.POST, "/stations", stationRequest)
                .body()
                .as(StationResponse.class);
    }
}
