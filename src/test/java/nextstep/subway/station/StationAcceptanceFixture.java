package nextstep.subway.station;

import static nextstep.subway.utils.RestAssuredUtils.*;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationAcceptanceFixture {

    public static final String PATH = "/stations";

    static ExtractableResponse<Response> 지하철역_생성_요청(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return post(PATH, params);
    }

    static ExtractableResponse<Response> 지하철역_조회_요청(final String path) {
        return get(path);
    }

    static ExtractableResponse<Response> 지하철역_삭제_요청(final String path) {
        return delete(path);
    }
}
