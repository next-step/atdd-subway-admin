package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.domain.Line;
import org.springframework.http.MediaType;

public class SubwayAppBehaviors {
    public static ExtractableResponse<Response> 지하철노선을_수정한다(String newName, String newColor, Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", newName);
        params.put("color", newColor);
        return RestAssured
                    .given().log().all()
                    .body(params)
                    .when().put("/lines/"+lineId)
                    .then().log().all()
                    .extract();
        }
    public static Line 지하철노선을_조회한다(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract().jsonPath().getObject(".", Line.class);
    }

    public static List<Line> 지하철노선목록을_조회한다() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList(".", Line.class);
    }

    public static Long 지하철노선을_생성하고_ID를_반환한다(
            String name, String color,
            String upStationName, String downStationName, int distance) {

        ExtractableResponse<Response> response = 지하철노선을_생성한다(name,color,upStationName,downStationName,distance);

        return response.jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철노선을_생성한다(
            String name, String color,
            String upStationName, String downStationName, int distance) {

        Long upStationId = 지하철역을_생성하고_생성된_ID를_반환한다(upStationName);
        Long downStationId = 지하철역을_생성하고_생성된_ID를_반환한다(downStationName);

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private static Long 지하철역을_생성하고_생성된_ID를_반환한다(String 역이름) {
        ExtractableResponse<Response> response = 지하철역을_생성한다(역이름);
        return response.jsonPath().getLong("id");
    }

    public static List<String> 지하철역_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    public static ExtractableResponse<Response> 지하철역을_생성한다(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역을_삭제한다(String location) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .extract();
    }
}
