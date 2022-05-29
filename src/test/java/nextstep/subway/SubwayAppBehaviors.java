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
    public static List<Line> 지하철노선목록을_조회한다() {
            return RestAssured
                    .given().log().all()
                    .when().get()
                    .then().log().all()
                    .extract().jsonPath().getList("", Line.class);
        }

    public static ExtractableResponse<Response> 지하철노선을_생성한다(
            String name, String color
            , int upStationId, int downStationId, int distance) {
        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("color",color);
        params.put("upStationId",upStationId);
        params.put("downStationId",downStationId);
        params.put("distance",distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static List<String> 지하철역_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }
    public static ExtractableResponse<Response> 지하철역을_생성한다(String name) {
        Map<String,String> params = new HashMap<>();
        params.put("name",name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역을_삭제한다(String location) {
        return  RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(location)
                .then().log().all()
                .extract();
    }
}
