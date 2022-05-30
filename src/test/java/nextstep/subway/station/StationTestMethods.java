package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;

import static nextstep.subway.testutils.RestAssuredMethods.*;

public class StationTestMethods {
    public static final String URI_STATIONS = "/stations";

    public static ExtractableResponse<Response> 지하철역_조회() {
        return get(URI_STATIONS);
    }

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        return post(URI_STATIONS, StationRequest.from(stationName));
    }

    public static ExtractableResponse<Response> 지하철역_삭제(String location) {
        return delete(location);
    }
}
