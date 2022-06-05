package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;
import org.springframework.http.MediaType;

public class Station {
    private Station() {
    }

    public static ExtractableResponse<Response> 지하철역_추가(String name) {
        StationRequest request = 지하철역_요청_생성(name);
        return 지하철역_추가(request);
    }

    static StationRequest 지하철역_요청_생성(String name) {
        return new StationRequest(name);
    }

    static ExtractableResponse<Response> 지하철역_추가(StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철역_아이디로_삭제(int id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철역_전체_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }
}
