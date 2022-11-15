package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.springframework.http.MediaType;

import static nextstep.subway.station.StationAcceptaneCommon.지하철_역_등록;

public class LineAcceptanceCommon {

    public static ExtractableResponse<Response> 지하철_노선_등록(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_등록(String name, String color, String upstationName, String downStationName, long distance) {
        long upStationId = 지하철_역_등록(upstationName).jsonPath().getLong("id");
        long downStationId = 지하철_역_등록(downStationName).jsonPath().getLong("id");
        return RestAssured.given().log().all()
                .body(LineRequest.builder()
                        .name(name)
                        .color(color)
                        .upStationId(upStationId)
                        .downStationId(downStationId)
                        .distance(distance)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_등록(String name, String color, long upStationId,long downStationId, long distance) {
        return RestAssured.given().log().all()
                .body(LineRequest.builder()
                        .name(name)
                        .color(color)
                        .upStationId(upStationId)
                        .downStationId(downStationId)
                        .distance(distance)
                        .build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_전체조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }
}
