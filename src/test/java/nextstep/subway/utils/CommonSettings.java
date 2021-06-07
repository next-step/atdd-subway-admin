package nextstep.subway.utils;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;

public class CommonSettings {

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return getPostReponse(request, "/lines");

    }

    public static ExtractableResponse<Response> 지하철_노선_목록_반환_요청() {
        return getGetReponse("/lines");
    }

    public static ExtractableResponse<Response> 지하철_조회_요청(String path) {
        return getGetReponse(path);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String path) {
        return getPutResponse(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10), path);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거(String path) {
        return getDeleteResponse(path);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest request) {
        return getPostReponse(request, "/stations");
    }

    private static ExtractableResponse<Response> getDeleteResponse(String path) {
        return RestAssured.given().log().all()
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> getPutResponse(Object request, String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> getGetReponse(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> getPostReponse(Object request, String path) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }
}
