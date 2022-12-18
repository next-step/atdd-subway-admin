package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import nextstep.subway.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class LineAcceptanceTestHelper {

    static ExtractableResponse<Response> createLine(Map<String, String> params) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    static JsonPath updateLine(String id, Map<String, String> params) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when().put("/lines/{id}", id)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract().jsonPath();
    }

    static JsonPath getLines() {
        return getLinesResponse().jsonPath();
    }

    static ExtractableResponse<Response> getLinesResponse() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract();
    }

    static JsonPath getLine(String id) {
        return getLineResponse(id).jsonPath();
    }

    static ExtractableResponse<Response> getLineResponse(String id) {
        return RestAssured.given().log().all()
            .when().get("/lines/{id}", id)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract();
    }

    static ExtractableResponse<Response> getLineResponse(LineResponse lineResponse) {
        return getLineResponse(lineResponse.getId().toString());
    }

    static void deleteLine(String id) {
        RestAssured.given().log().all()
            .when().delete("/lines/{id}", id)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
