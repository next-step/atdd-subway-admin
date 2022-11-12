package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationAcceptanceFixture {

    public static ValidatableResponse 지하철역_생성(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }

    public static List<String> 지하철역_이름_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    public static void 지하철역_삭제(Long stationId) {
        RestAssured.given().log().all()
                .pathParam("id", stationId)
                .when().delete("/stations/{id}")
                .then().log().all();
    }

    public static JsonPath 제이슨_경로_얻기(ValidatableResponse validatableResponse) {
        return validatableResponse.extract().jsonPath();
    }

    public static long 지하철역_아이디_조회(ValidatableResponse response) {
        return Long.parseLong(response.extract().jsonPath().get("id").toString());
    }
}
