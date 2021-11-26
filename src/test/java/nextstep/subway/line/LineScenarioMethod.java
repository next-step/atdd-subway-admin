package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.assured.RestAssuredApi;
import nextstep.subway.line.dto.LineEditRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.HttpStatus.*;

class LineScenarioMethod {

    public static LineRequest 지하철_노선_정보(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssuredApi.post("/lines", request);
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response, int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    public static String 지하철_노선_등록되어_있음(LineRequest request) {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(request);
        return createResponse.header("Location");
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(String uri) {
        return RestAssuredApi.get(uri);
    }

    public static void 지하철_노선_조회_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    public static void 지하철_노선_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    public static void 지하철_노선_목록_조회_결과_포함됨(ExtractableResponse<Response> response, LineRequest request) {
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        assertThat(lineResponses)
                .extracting("name", "color")
                .contains(tuple(request.getName(), request.getColor()));
    }

    public static void 지하철_노선_조회_결과_일치됨(ExtractableResponse<Response> response, LineRequest request) {
        assertThat(response.jsonPath().getObject(".", LineResponse.class))
                .extracting("name", "color")
                .containsExactly(request.getName(), request.getColor());
    }

    public static void 지하철_노선_구간_정렬됨(ExtractableResponse<Response> response, Map<String, Long> stations) {
        List<Long> stationIds = response.jsonPath().getList("stations.id", Long.class);
        Long[] exceptedIds = new ArrayList<>(stations.values()).toArray(new Long[0]);
        assertThat(stationIds).containsExactly(exceptedIds);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String uri, LineEditRequest request) {
        return RestAssuredApi.put(uri, request);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    public static void 지하철_노선_수정_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    public static void 지하철_노선_수정_결과_일치됨(String uri, LineEditRequest request) {
        ExtractableResponse<Response> updatedResponse = 지하철_노선_조회_요청(uri);
        assertThat(updatedResponse.jsonPath().getObject(".", LineResponse.class))
                .extracting("name", "color")
                .contains(request.getName(), request.getColor());
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssuredApi.delete(uri);
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    public static void 지하철_노선_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }
}
