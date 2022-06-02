package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.station.StationRequest;
import org.springframework.http.MediaType;

import java.util.List;

public class SubwayApi {

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);

        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제(Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/{stationId}", stationId)
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철역_이름_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    public static LineResponse convertLineResponse(ExtractableResponse<Response> response) {
        return response.body().as(LineResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성(String lineName, String color, int distance,
                                                          String upStationName, String downStationName) {
        Long upStationId = 지하철역_생성(upStationName).jsonPath().getLong("id");
        Long downStationId = 지하철역_생성(downStationName).jsonPath().getLong("id");

        LineRequest lineRequest = new LineRequest(lineName, color, distance, upStationId, downStationId);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철역_노선_이름_목록조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    public static ExtractableResponse<Response> 지하철역_노선_정보_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_노선_수정(Long lineId, String lineName, String color) {
        LineRequest lineRequest = new LineRequest();
        lineRequest.setName(lineName);
        lineRequest.setColor(color);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }
}
