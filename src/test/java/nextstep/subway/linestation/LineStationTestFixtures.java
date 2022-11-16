package nextstep.subway.linestation;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public abstract class LineStationTestFixtures {

    private static final String PATH_LINE = "/lines";
    private static final String PATH_LINE_STATION = "/line-station";
    private static final String PATH_LINE_ID = PATH_LINE_STATION + "/{lineId}";

    public static void 새로운_길이를_뺀_나머지를_새롭게_추가된_역과의_길이로_설정(String information, String pathVariable,
                                                         String... containValues) {
        List<String> informationList = 목록조회(information, PATH_LINE_ID, pathVariable);
        assertThat(informationList).contains(containValues);
    }

    public static ExtractableResponse<Response> 역_사이_새로운역_등록(String upStationId, String downStationId, String distance,
                                                             String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID, pathVariable);
    }

    public static String 지하철_노선_등록되어_있음(String name, String color, String upStationId, String downStationId,
                                        String distance, String returnValue) {
        return 생성_값_리턴(노선(name, color, upStationId, downStationId, distance), PATH_LINE, returnValue);
    }

    private static List<String> 목록조회(String information, String path, String pathVariable) {
        return RestAssured.given().log().all()
                .when().get(path, pathVariable)
                .then().log().all()
                .extract().jsonPath().getList(information, String.class);
    }

    private static String 생성_값_리턴(Map<String, String> paramMap, String path, String pathVariable, String returnValue) {
        return 생성(paramMap, path, pathVariable).jsonPath().getString(returnValue);
    }

    private static String 생성_값_리턴(Map<String, String> paramMap, String path, String returnValue) {
        return 생성(paramMap, path).jsonPath().getString(returnValue);
    }

    private static ExtractableResponse<Response> 생성(Map<String, String> paramMap, String path, String pathVariable) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path, pathVariable)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 생성(Map<String, String> paramMap, String path) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    private static Map<String, String> 구간(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
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
}
