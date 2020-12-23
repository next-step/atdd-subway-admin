package nextstep.subway.line.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceStep {
    public static ExtractableResponse<Response> 새로운_지하철_노선_생성_요청(
            final String lineName, final String lineColor
    ) {
        LineRequest lineRequest = new LineRequest(lineName, lineColor);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 특정_지하철_노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineId)
                .then()
                .log().all()
                .extract();
    }

    public static void 새로운_지하철_노선_생성됨(
            final ExtractableResponse<Response> response, final String lineName, final String lineColor
    ) {
        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotEmpty();
        assertThat(lineResponse.getColor()).isEqualTo(lineColor);
        assertThat(lineResponse.getName()).isEqualTo(lineName);
    }

    public static void 응답에_지하철_노선들이_포함되어_있음(
            ExtractableResponse<Response> line1CreatedResponse, ExtractableResponse<Response> line2CreatedResponse,
            ExtractableResponse<Response> listResponse
    ) {
        List<Long> expectedLineIds = Arrays.asList(line1CreatedResponse, line2CreatedResponse).stream()
                .map(LineAcceptanceStep::응답_헤더에서_ID_추출)
                .collect(Collectors.toList());
        List<Long> resultLineIds = listResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static ExtractableResponse<Response> 지하철_노선_변경_요청(Long lineId, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then()
                .log().all()
                .extract();
    }

    public static Long 응답_헤더에서_ID_추출(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("location").split("/")[2]);
    }
}
