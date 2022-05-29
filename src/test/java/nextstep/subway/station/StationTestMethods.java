package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;
import org.springframework.http.MediaType;

public class StationTestMethods {
    public static final String URI_STATIONS = "/stations";

    public static ExtractableResponse<Response> 지하철역_조회() {
        return RestAssured.given().log().all()
                .when().get(URI_STATIONS)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        return RestAssured.given().log().all()
                .body(StationRequest.from(stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(URI_STATIONS)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제(String location) {
        return RestAssured.given().log().all()
                .when().delete(location)
                .then().log().all()
                .extract();
    }
}
