package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

public class Fixture {

    public static LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600");
    public static LineRequest 이호선 = new LineRequest("신분당선", "bg-red-600");
    public static StationRequest 강남역 = new StationRequest("강남역");
    public static StationRequest 광교역 = new StationRequest("광교역");
    public static StationRequest 홍대역 = new StationRequest("홍대역");
    public static StationRequest 신촌역 = new StationRequest("신촌역");
    public static StationRequest 판교역 = new StationRequest("판교역");

    public static ExtractableResponse<Response> get(String path, Object... params) {
        return response(request().get(path, params));
    }

    public static ExtractableResponse<Response> post(String path, Object request) {
        return response(request(request).post(path));
    }

    public static ExtractableResponse<Response> post(String path, Object request, Object... params) {
        return response(request(request).post(path, params));
    }

    public static ExtractableResponse<Response> delete(String path, Object... params) {
        return response(request().delete(path, params));
    }

    public static ExtractableResponse<Response> put(String path, Object request, Object... params) {
        return response(request(request).put(path, params));
    }

    private static RequestSpecification request(Object body) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body);
    }

    private static RequestSpecification request() {
        return RestAssured.given().log().all().when();
    }

    private static ExtractableResponse<Response> response(Response response) {
        return response.then().log().all().extract();
    }

}
