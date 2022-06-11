package nextstep.subway.util;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import org.springframework.http.MediaType;

public class LineAcceptanceMethods {
    public static ExtractableResponse<Response> createLine(String lineName, String lineColor, Long upStationId,
                                                           Long downStationId, Integer distance) {
        LineRequest request = new LineRequest(lineName, lineColor, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateLine(Long id, String lineName, String lineColor) {
        LineRequest request = new LineRequest(lineName, lineColor, null, null, null);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> addSection(Long lineId, Long upStationId, Long downStationId,
                                                           Integer distance) {
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getAllLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/")
                .then().log().all()
                .extract();
    }
}
