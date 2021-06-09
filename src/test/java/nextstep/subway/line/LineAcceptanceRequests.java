package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceRequests {
    static final String NEW_BUNDANG_LINE_COLOR = "bg-red-600";
    static final String NEW_BUNDANG_LINE_NAME = "신분당선";
    static final String SECOND_LINE_COLOR = "bg-green-600";
    static final String SECOND_LINE_NAME = "2호선";
    static final String OLD_BUNDANG_LINE_COLOR = "bg-blue-600";
    static final String OLD_BUNDANG_LINE_NAME = "구분당선";

    static ExtractableResponse<Response> requestCreateLine(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    static ExtractableResponse<Response> requestShowLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> requestShowLine(String lineId) {
        String path = "/lines/" + lineId;
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> requestUpdateLine(String uri, String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> requestDeleteLine(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
