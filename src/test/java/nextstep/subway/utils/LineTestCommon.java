package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTestCommon {

    /**
     * Line 파라미터 생성
     * @param name
     * @param color
     * @return LineRequest params
     */
    public static LineRequest createLineParams(String name, String color, Long upStationId, Long downStationId, Long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    /**
     * 지하철 노선 생성 요청
     * @param params
     * @param path
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> createResponse(LineRequest params, String path) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    /**
     * 전체 조회 요청
     * @param path
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> findAllResponse(String path) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 조회 요청
     * @param id
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> findOneResponse(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + id)
                .then()
                .log()
                .all()
                .extract();
    }

    /**
     * 지하철 노선 수정 요청
     * @param id
     * @param request
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> updateResponse(LineRequest request, Long id) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 제거 요청
     * @param id
     * @return ExtractableResponse<Response>
     */
    public static ExtractableResponse<Response> deleteResponse(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철_노선_조회됨(ExtractableResponse<Response> extractableResponse, LineRequest request) {
        LineResponse linesResponse = extractableResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(linesResponse.getName()).isEqualTo(request.getName());
        assertThat(linesResponse.getColor()).isEqualTo(request.getColor());
        assertThat(linesResponse.getStations().size()).isEqualTo(2);
    }

    public static void 지하철_노선_수정됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선_삭제됨(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
