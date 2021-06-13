package nextstep.subway.line;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

public class LineAcceptanceMethods {

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return 지하철_노선_생성_요청(lineRequest);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured
            .given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/{id}", createResponse.jsonPath().getLong("id"))
            .then().log().all().extract();
        return response;
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(
        ExtractableResponse<Response> createResponse, LineRequest lineRequest) {
        return RestAssured
            .given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{id}", createResponse.jsonPath().getLong("id"))
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
            .given().log().all()
            .when().delete("/lines/{id}", createResponse.jsonPath().getLong("id"))
            .then().log().all().extract();
    }

    public static void 지하철_노선_생성됨(LineRequest lineRequest, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).isNotBlank();

        지하철_노선_정보_검증(lineRequest, response.as(LineResponse.class));
    }

    private static void 지하철_노선_정보_검증(LineRequest lineRequest, LineResponse lineResponse) {
        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
        assertThat(lineResponse.getStations()).isNotNull();
        assertThat(lineResponse.getStations().size()).isNotZero();
        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(lineRequest.getUpStationId());
        assertThat(lineResponse.getStations().get(1).getId()).isEqualTo(lineRequest.getDownStationId());
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_응답됨(LineRequest lineRequest, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        LineResponse lineResponse = response.jsonPath()
            .getList(".", LineResponse.class)
            .get(0);

        지하철_노선_정보_검증(lineRequest, lineResponse);
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
        ExtractableResponse<Response>... createResponses) {
        List<Long> expectedLineIds = Arrays.stream(createResponses)
            .map(res -> res.jsonPath().getLong("id"))
            .collect(toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(res -> res.getId())
            .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response, LineRequest lineRequest) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        지하철_노선_정보_검증(lineRequest, response.as(LineResponse.class));
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> updateResponse,
        ExtractableResponse<Response> retryResponse, LineRequest lineRequest) {
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = retryResponse.as(LineResponse.class);
        assertThat(retryResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(retryResponse.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response,
        ExtractableResponse<Response> notFoundResponse) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(notFoundResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
