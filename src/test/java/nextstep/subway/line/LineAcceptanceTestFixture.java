package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.MediaType;

public class LineAcceptanceTestFixture {

    public static ExtractableResponse<Response> 지하철_노선_생성(String name, String color,
        Long upStationId, Long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, distance, upStationId, downStationId);

        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all()
            .extract();
    }
}
