package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.assured.RestAssuredApi;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Long 지하철_종점역_생성됨(StationRequest request) {
        ExtractableResponse<Response> response = 지하철_역_생성_요청(request);
        return response.as(StationResponse.class).getId();
    }

    public static Map<String, Long> 지하철_종점역_정보(String upStationName, String downStationName) {
        HashMap<String, Long> terminus = new HashMap<>();
        terminus.put("upStationId", 지하철_종점역_생성됨(new StationRequest(upStationName)));
        terminus.put("downStationId", 지하철_종점역_생성됨(new StationRequest(downStationName)));
        return terminus;
    }

    public static Map<String, Long> 등록되지_않은_지하철_종점역() {
        HashMap<String, Long> terminus = new HashMap<>();
        terminus.put("upStationId", 1L);
        terminus.put("downStationId", 2L);
        return terminus;
    }
}
