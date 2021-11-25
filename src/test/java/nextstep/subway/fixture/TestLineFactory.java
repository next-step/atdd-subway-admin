package nextstep.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TestLineFactory {

    public static final LineRequest LINE_REQUEST = LineRequest.of("신분당선", "bg-red-600", 1L, 2L, 10);
    public static final LineRequest ANOTHER_LINE_REQUEST = LineRequest.of("bg-red-600", "2호선", 3L, 4L, 10);

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철들_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(final ExtractableResponse<Response> createResponse, final String color
            , final String name) {
        final Long id = 지하철_노선_ID_추출(createResponse);
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/"+id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(final ExtractableResponse<Response> createResponse) {
        final Long id = 지하철_노선_ID_추출(createResponse);
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/"+id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(final ExtractableResponse<Response> createResponse) {
        final Long id = 지하철_노선_ID_추출(createResponse);
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static Long 지하철_노선_ID_추출(final ExtractableResponse<Response> createResponse1) {
        return Arrays.asList(createResponse1).stream()
                .map(res -> Long.parseLong(res.header("Location").split("/")[2]))
                .findFirst().get();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );

    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2)
                .stream()
                .map(res -> Long.parseLong(res.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_구간_확인됨(ExtractableResponse<Response> lineResponse) {
        LineResponse response = lineResponse.as(LineResponse.class);
        assertThat(response.getStationResponses().size()).isEqualTo(2);
    }
}
