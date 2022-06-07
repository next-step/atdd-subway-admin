package nextstep.subway.utils;

import static nextstep.subway.station.StationAcceptanceTest.BASE_URL;
import static nextstep.subway.utils.AssertionsUtils.assertCreated;
import static nextstep.subway.utils.RequestParamUtils.generateRequestParam;
import static nextstep.subway.utils.RestAssuredUtils.post;

import io.restassured.response.Response;
import java.util.Map;

public class StationsUtils {

    public static final String NAME = "name";

    public static Response generateStation(final String stationName) {
        Map<String, String> requestParam = generateRequestParam(NAME, stationName);
        Response response = post(BASE_URL, requestParam).extract().response();
        assertCreated(response);
        return response;
    }
}
