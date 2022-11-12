package nextstep.subway.station;

import static nextstep.subway.util.FixtureUtil.getIdFromLocation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class StationFixture {

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_생성(final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return 지하철역_생성(params);
    }

    public static ExtractableResponse<Response> 지하철역_생성(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제(final Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/stations/" + id)
            .then().log().all()
            .extract();
    }

    public static Long 지하철역_생성후_아이디_반환(final String name) {
        ExtractableResponse<Response> response = 지하철역_생성(name);
        return getIdFromLocation(response.header(HttpHeaders.LOCATION));
    }

}
