package nextstep.acceptence.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceFixture {

    private static final String DEFAULT_URL = "/stations";
    private static final String SLASH = "/";


    public static ExtractableResponse<Response> 지하철역을_생성한다(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    public static long 지하철역_생성후_ID_를_리턴한다(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();

        Object getId = response.jsonPath().get("id");

        return Long.parseLong(String.valueOf(getId));
    }

    public static List<String> 모든_지하철역을_조회한다(String target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getList(target, String.class);
    }

    public static ExtractableResponse<Response> 지하철역을_삭제한다(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(DEFAULT_URL + SLASH + +id)
                .then().log().all()
                .extract();
    }

    public static void 지하철역_이름이_조회된다(List<String> allStations, String expect) {
        assertThat(allStations).containsAnyOf(expect);
    }

    public static void 상태코드를_체크한다(int statusCode, int expect) {
        assertThat(statusCode).isEqualTo(expect);
    }

    public static void 조회한_지하철역의_사이즈를_조회한다(List<String> stations, int expect) {
        assertThat(stations).hasSize(expect);
    }

}
