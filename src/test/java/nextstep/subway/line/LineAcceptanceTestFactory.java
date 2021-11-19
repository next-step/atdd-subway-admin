package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.RestAssuredApiTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceTestFactory {
    private static final String LINE_BASE_API_URL = "/lines";

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        final Map<String, String> params = getLineCreateParams(name, color);
        return 지하철_노선_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssuredApiTest.create(LINE_BASE_API_URL, params);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssuredApiTest.fetch(LINE_BASE_API_URL);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssuredApiTest.fetch(LINE_BASE_API_URL, id);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssuredApiTest.delete(LINE_BASE_API_URL, id);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, Map<String, String> params) {
        return RestAssuredApiTest.update(LINE_BASE_API_URL, id, params);
    }

    public static Map<String, String> getLineCreateParams(String name, String color) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return Collections.unmodifiableMap(params);
    }

}
