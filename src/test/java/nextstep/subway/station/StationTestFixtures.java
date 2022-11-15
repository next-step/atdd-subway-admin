package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public abstract class StationTestFixtures {

    private static final String PATH = "/stations";

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        return 생성(지하철역(stationName), PATH);
    }

    public static List<String> 지하철역_목록조회(String information) {
        return 목록조회(information, PATH);
    }

    public static ExtractableResponse<Response> 지하철역_삭제(String path, String pathVariable) {
        return 삭제(PATH + path, pathVariable);
    }

    public static String 지하철역_생성_값_리턴(String stationName, String value) {
        return 생성_값_리턴(지하철역(stationName), PATH, value);
    }

    private static Map<String, String> 지하철역(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return params;
    }

    private static ExtractableResponse<Response> 생성(Map<String, String> paramMap, String path) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    private static String 생성_값_리턴(Map<String, String> paramMap, String path, String value) {
        return 생성(paramMap, path).jsonPath().getString(value);
    }

    private static List<String> 목록조회(String information, String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract().jsonPath().getList(information, String.class);
    }

    private static ExtractableResponse<Response> 삭제(String path, String pathVariable) {
        return RestAssured.given().log().all()
                .when().delete(path, pathVariable)
                .then().log().all()
                .extract();
    }
}
