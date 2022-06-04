package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@Sql("/truncate.sql")
public abstract class BaseLineAcceptanceTest {
    private static final String LINES_URI = "/lines";

    protected ExtractableResponse<Response> createLineRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINES_URI)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> findLineRequest(int lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().get(LINES_URI + "/{id}")
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> findAllLinesRequest() {
        return RestAssured.given().log().all()
                .when().get(LINES_URI)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> updateLineRequest(int lineId, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().put(LINES_URI + "/{id}")
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> deleteLineRequest(int lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().delete(LINES_URI + "/{id}")
                .then().log().all()
                .extract();
    }
}
