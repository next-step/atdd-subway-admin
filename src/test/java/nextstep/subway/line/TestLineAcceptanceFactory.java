package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

public class TestLineAcceptanceFactory {

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long deleteLineId) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", deleteLineId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        LineRequest lineRequest = 지하철_노선_파라미터_생성(name, color);

        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long createLineId, LineRequest updateParams) {
        return RestAssured
                .given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().patch("/lines/{id}", createLineId)
                .then().log().all().extract();
    }

    public static LineRequest 지하철_노선_파라미터_생성(String name, String color) {
        return LineRequest.of(name, color);
    }
}
