package nextstep.subway.section;

import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class SectionAcceptanceTestFixture {
    public static ExtractableResponse<Response> 지하철_구간_등록(Integer id, Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/" + id + "/sections")
            .then().log().all()
            .extract();
    }
}
