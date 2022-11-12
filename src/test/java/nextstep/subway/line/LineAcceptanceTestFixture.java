package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTestFixture {
    public static final String LINE_API_MAIN_PATH = "/lines";

    public static ExtractableResponse<Response> createLine(String name, String color, int distance, Long upStationId,
                                                           Long downStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("distance", distance);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_API_MAIN_PATH)
                .then().assertThat().statusCode(HttpStatus.CREATED.value()).log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findAllLines() {
        return RestAssured.given().log().all()
                .when().get(LINE_API_MAIN_PATH)
                .then().assertThat().statusCode(HttpStatus.OK.value()).log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findLine(Long id) {
        return RestAssured.given().log().all()
                .when().get(LINE_API_MAIN_PATH + "/{id}", id)
                .then().assertThat().statusCode(HttpStatus.OK.value()).log().all()
                .extract();
    }
}
