package nextstep.subway.utils;

import static nextstep.subway.utils.RestAssuredUtils.delete;
import static nextstep.subway.utils.RestAssuredUtils.get;

import io.restassured.response.Response;
import nextstep.subway.dto.station.StationRequest;

public class StationsAcceptanceUtils {

    private static final String STATION_BASE_URL = "/stations";

    public static Response 지하철역_생성_요청(final String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);
        return RestAssuredUtils.post(STATION_BASE_URL, stationRequest).extract().response();
    }

    public static Response 지하철역_목록_조회_요청() {
        return get(STATION_BASE_URL).extract().response();
    }

    public static Response 지하철역_삭제_요청(final Long id) {
        return delete(STATION_BASE_URL, id).extract().response();
    }
}
