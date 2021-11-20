package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.assured.RestAssuredApi;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.*;

class LineScenarioMethod {

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssuredApi.post("/lines", request);
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    public static String 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineRequest);
        return createResponse.header("Location");
    }

    public static void 지하철_노선이_등록되어_있지_않음(Long id) {
        ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청("/lines/" + id);
        지하철_노선_조회_실패됨(findResponse);
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

    public static void 지하철_노선_목록_조회_결과에_포함됨(ExtractableResponse<Response> response, LineRequest request) {
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        AssertionsForInterfaceTypes.assertThat(lineResponses)
                .extracting(LineResponse::getColor, LineResponse::getName)
                .contains(Assertions.tuple(request.getColor(), request.getName()));
    }

    public static void 지하철_노선_조회_결과_일치됨(ExtractableResponse<Response> response, LineRequest request) {
        assertThat(response.jsonPath().getObject(".", LineResponse.class))
                .extracting("name", "color")
                .contains(request.getName(), request.getColor());
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest request) {
        return RestAssuredApi.put("lines/" + id, request);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssuredApi.delete("lines/" + id);
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    public static void 지하철_노선_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }
}
