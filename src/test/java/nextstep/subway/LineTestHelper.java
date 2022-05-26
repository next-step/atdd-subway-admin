package nextstep.subway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineTestHelper {

    public static ExtractableResponse<Response> 노선_생성_요청(String name, String color, int distance,
        Long upStationId, Long downStationId) {
        LineRequest requestBody = new LineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
            .body(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_목록_요청() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    public static void 노선_목록_확인(ExtractableResponse<Response> response, String... lineNames) {
        List<String> requestLineNames = response.body().jsonPath().getList("name", String.class);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.statusCode()),
            () -> assertThat(lineNames).containsAll(requestLineNames)
        );
    }

    public static ExtractableResponse<Response> 노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/{id}", id)
            .then().log().all()
            .extract();
    }

    public static void 노선_조회_확인(String 신분당선, String color, int distance,
        ExtractableResponse<Response> 노선_조회_응답) {

        ResponseBodyExtractionOptions body = 노선_조회_응답.body();
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), 노선_조회_응답.statusCode()),
            () -> assertThat(body.jsonPath().getLong("id")).isNotNull(),
            () -> assertEquals(신분당선, body.jsonPath().getString("name")),
            () -> assertEquals(color, body.jsonPath().getString("color"))
        );
    }

    public static ExtractableResponse<Response> 노선_수정_요청(Long id, String name, String color, int distance) {

        LineRequest requestBody = new LineRequest(name, color, null, null, distance);

        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(requestBody)
            .when().put("/lines/{id}", id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
            .when().delete("/lines/{id}", id)
            .then().log().all()
            .extract();
    }
}
