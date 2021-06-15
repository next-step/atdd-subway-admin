package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceRequests {

    static ExtractableResponse<Response> requestCreateLine(LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("color", lineRequest.getColor());
        params.put("name", lineRequest.getName());
        params.put("upStationId", lineRequest.getUpStationId().toString());
        params.put("downStationId", lineRequest.getDownStationId().toString());
        params.put("distance", Integer.toString(lineRequest.getDistance()));

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

    static ExtractableResponse<Response> requestShowLine(Long lineId) {
        String path = "/lines/" + lineId.toString();
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> requestUpdateLine(String uri, LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("color", lineRequest.getColor());
        params.put("name", lineRequest.getName());
        params.put("upStationId", lineRequest.getUpStationId().toString());
        params.put("downStationId", lineRequest.getDownStationId().toString());
        params.put("distance", Integer.toString(lineRequest.getDistance()));

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
