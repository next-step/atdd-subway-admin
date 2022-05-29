package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceFactory;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceFactory {

    public static ExtractableResponse<Response> 지하철노선_생성(String lineName, String upStationName, String downStationName, String color) {
        Map<String, Object> params = 지하철노선_정보_생성(lineName, upStationName, downStationName, color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

    }
    private static Map<String, Object> 지하철노선_정보_생성(String lineName, String upStationName, String downStationName, String color) {
        Long upStationId = StationAcceptanceFactory.지하철역_생성(upStationName)
                .jsonPath().getObject("id", Long.class);

        Long downStationId = StationAcceptanceFactory.지하철역_생성(downStationName)
                .jsonPath().getObject("id", Long.class);

        Map<String, Object> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 10);

        return params;
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static LineResponse ID값으로_지하철노선_조회(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract()
                .jsonPath().getObject(".", LineResponse.class);
    }

}
