package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredApiTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTestFactory.지하철_역_등록되어_있음;

public class LineAcceptanceTestFactory {
    private static final String LINE_BASE_API_URL = "/lines";
    private static final String LINE_DELETE_API_URL = LINE_BASE_API_URL + "/{id}";
    private static final String LINE_UPDATE_API_URL = LINE_BASE_API_URL + "/{id}";
    private static final String LINE_ADD_SECTION_URL = LINE_BASE_API_URL + "/{lineId}/sections";
    private static final String LINE_DELETE_STATION_API_URL = LINE_BASE_API_URL + "/{id}/sections";

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color, Long upStationId, Long downStationId, int distance) {
        final Map<String, String> params = getLineCreateParams(name, color, upStationId, downStationId, distance);
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        return 지하철_노선_조회_요청(response.jsonPath().getLong("id"));
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssuredApiTest.post(LINE_BASE_API_URL, params);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssuredApiTest.get(LINE_BASE_API_URL);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssuredApiTest.get(LINE_BASE_API_URL, id);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssuredApiTest.delete(LINE_DELETE_API_URL, id);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, Map<String, String> params) {
        return RestAssuredApiTest.update(LINE_UPDATE_API_URL, id, params);
    }

    public static ExtractableResponse<Response> 지하철_구간_추가_요청(Long id, Map<String, String> params) {
        return RestAssuredApiTest.post(LINE_ADD_SECTION_URL, id, params);
    }

    public static void 지하철_구간_추가_되어있음(Long lineId, Long upStation, String downStationName, int distance) {
        final Long stationId = 지하철_역_등록되어_있음(downStationName).as(StationResponse.class).getId();
        지하철_구간_추가_요청(lineId, getAddSectionParams(upStation, stationId, distance));
    }

    public static ExtractableResponse<Response> 지하철_구간_역_삭제_요청(Long id, Long stationId) {
        final Map<String, String> params = new HashMap<>();
        params.put("key", "stationId");
        params.put("value", stationId + "");
        return RestAssuredApiTest.delete(LINE_DELETE_STATION_API_URL, id, params);
    }

    public static Map<String, String> getAddSectionParams(Long upStationId, Long downStationId, int distance) {
        final Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return Collections.unmodifiableMap(params);
    }

    public static Map<String, String> getLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return Collections.unmodifiableMap(params);
    }

}
