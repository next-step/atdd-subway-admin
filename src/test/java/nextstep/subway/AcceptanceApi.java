package nextstep.subway;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;

public class AcceptanceApi {
    public static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest params) {
        return POST_요청("/stations", params);
    }

    public static ExtractableResponse<Response> 지하철_역_조회_요청() {
        return GET_요청("/stations");
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(String url) {
        return DELETE_요청(url);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest params) {
        return POST_요청("/lines", params);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return GET_요청("/lines");
    }


    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return GET_요청("/lines/" + id);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest params) {
        return PUT_요청("/lines/" + id, params);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return DELETE_요청("/lines/" + id);
    }

    private static ExtractableResponse<Response> GET_요청(String url) {
        return RestAssured.given().log().all()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> POST_요청(String url, Object params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(url)
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> PUT_요청(String url, Object params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(url)
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> DELETE_요청(String url) {
        return RestAssured.given().log().all()
            .when()
            .delete(url)
            .then().log().all()
            .extract();
    }
}
