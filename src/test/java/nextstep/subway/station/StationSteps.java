package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class StationSteps {

    public static ExtractableResponse<Response> 지하철_역_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
