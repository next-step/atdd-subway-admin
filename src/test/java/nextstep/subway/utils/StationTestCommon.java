package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

public class StationTestCommon {

    public static ExtractableResponse<Response> 지하철_역_생성_요청(String stationName) {
        return RestAssured.given().log().all()
                .body(new StationRequest(stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static StationResponse 지하철_역_생성됨(String stationName) {
        return 지하철_역_생성_요청(stationName)
                .jsonPath()
                .getObject(".", StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_역_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
