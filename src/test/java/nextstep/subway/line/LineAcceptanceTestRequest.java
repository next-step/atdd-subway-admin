package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.StationAcceptanceTestRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTestRequest {

    private LineAcceptanceTestRequest(){

    }

    public static ExtractableResponse<Response> createLine(String name, String color, String upStationName, String downStationName, int distance) {
        LineRequest request = createLineRequest(name, color, upStationName, downStationName, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest request = createLineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static LineRequest createLineRequest(String name, String color, String upStationName, String downStationName, int distance) {
        Long upStationId = searchStationId(upStationName);
        Long downStationId = searchStationId(downStationName);
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public static LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public static ExtractableResponse<Response> selectAllLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> selectOneLine(String lineNumber) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineNumber)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestModifyLine(ExtractableResponse<Response> createResponse1, String id, String name, String color, String upStationName, String downStationName, int distance) {
        LineRequest request = LineAcceptanceTestRequest.createLineRequest(name, color, upStationName, downStationName, distance);
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(createResponse1.header("Location"))
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> removeLine(String lineNumber) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineNumber)
                .then().log().all()
                .extract();
    }



    private static Long searchStationId(String name) {
        ExtractableResponse<Response> createResponse = StationAcceptanceTestRequest.createStation(name);
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }


}
