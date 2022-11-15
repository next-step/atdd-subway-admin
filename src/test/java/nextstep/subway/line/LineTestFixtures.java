package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public abstract class LineTestFixtures {

    private static final String PATH = "/lines";

    public static ExtractableResponse<Response> 노선_생성(String name, String color, String upStationId,
                                                      String downStationId,
                                                      String distance) {
        return 생성(노선(name, color, upStationId, downStationId, distance), PATH);
    }

    public static List<String> 노선_목록조회(String information) {
        return 목록조회(information, PATH);
    }

    public static ExtractableResponse<Response> 노선_수정(String name, String color, String upStationId,
                                                      String downStationId,
                                                      String distance, String path, String pathVariable) {
        return 수정(노선(name, color, upStationId, downStationId, distance), PATH + path, pathVariable);
    }

    public static String 노선_조회(String path, String pathVariable, String information) {
        return RestAssured.given().log().all()
                .when().get(PATH + path, pathVariable)
                .then().log().all()
                .extract().jsonPath().getString(information);
    }

    public static String 노선_생성_값_리턴(String name, String color, String upStationId, String downStationId,
                                    String distance, String value) {
        return 생성_값_리턴(노선(name, color, upStationId, downStationId, distance), PATH, value);
    }

    public static ExtractableResponse<Response> 노선_삭제(String path, String pathVariable) {
        return 삭제(PATH + path, pathVariable);
    }

    private static Map<String, String> 노선(String name, String color, String upStationId, String downStationId,
                                          String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
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

    private static ExtractableResponse<Response> 수정(Map<String, String> paramMap, String path, String pathVariable) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path, pathVariable)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 삭제(String path, String pathVariable) {
        return RestAssured.given().log().all()
                .when().delete(path, pathVariable)
                .then().log().all()
                .extract();
    }
}
