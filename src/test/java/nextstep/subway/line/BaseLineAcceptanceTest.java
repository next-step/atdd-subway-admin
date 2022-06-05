package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.BaseAcceptacneTest;
import org.springframework.http.MediaType;

public abstract class BaseLineAcceptanceTest extends BaseAcceptacneTest {
    private static final String LINES_URI = "/lines";

    public static ExtractableResponse<Response> createLineRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINES_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findLineRequest(int lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().get(LINES_URI + "/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findAllLinesRequest() {
        return RestAssured.given().log().all()
                .when().get(LINES_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateLineRequest(int lineId, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().put(LINES_URI + "/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteLineRequest(int lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().delete(LINES_URI + "/{id}")
                .then().log().all()
                .extract();
    }
}
