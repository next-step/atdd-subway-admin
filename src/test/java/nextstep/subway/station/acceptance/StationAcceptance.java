package nextstep.subway.station.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationAcceptance {
    public static ExtractableResponse<Response> create_station(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static List<String> station_list_was_queried() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name", String.class);
    }

    public static Long getStationId(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> delete_station(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/stations/" + id)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }
}
