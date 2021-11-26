package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.assured.RestAssuredApi;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.HttpStatus.*;

public class StationScenarioMethod {

    public static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest request) {
        return RestAssuredApi.post("/stations", request);
    }

    public static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    public static String 지하철_역_등록되어_있음(StationRequest request) {
        ExtractableResponse<Response> createResponse = 지하철_역_생성_요청(request);
        return createResponse.header("Location");
    }

    public static ExtractableResponse<Response> 지하철_역_조회_요청(String uri) {
        return RestAssuredApi.get(uri);
    }

    public static void 지하철_역_조회_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    public static void 지하철_역_목록_조회_결과_포함됨(ExtractableResponse<Response> response, StationRequest request) {
        List<StationResponse> stationResponses = response.jsonPath().getList(".", StationResponse.class);
        assertThat(stationResponses)
                .extracting(StationResponse::getName)
                .contains(request.getName());
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(String uri) {
        return RestAssuredApi.delete(uri);
    }

    public static void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    public static Long 생성된_지하철_역_ID(StationRequest request) {
        ExtractableResponse<Response> response = 지하철_역_생성_요청(request);
        return response.as(StationResponse.class).getId();
    }

    public static Map<String, Long> 지하철_역_정보(String upStationName, String downStationName) {
        HashMap<String, Long> terminus = new HashMap<>();
        terminus.put(upStationName, 생성된_지하철_역_ID(new StationRequest(upStationName)));
        terminus.put(downStationName, 생성된_지하철_역_ID(new StationRequest(downStationName)));
        return terminus;
    }

    public static Map<String, Long> 등록되지_않은_지하철_역() {
        HashMap<String, Long> terminus = new HashMap<>();
        terminus.put("상행", 1L);
        terminus.put("하행", 2L);
        return terminus;
    }

    public static TreeMap<String, Long> 지하철_역_여러개_등록되어_있음(StationRequest... request) {
        TreeMap<String, Long> stationIds = new TreeMap<>();
        for (StationRequest req : request) {
            Long stationId = 생성된_지하철_역_ID(req);
            stationIds.put(req.getName(), stationId);
        }
        return stationIds;
    }
}
