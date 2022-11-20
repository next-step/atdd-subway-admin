package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public abstract class SectionTestFixtures {

    private static final String PATH_LINE = "/lines";
    private static final String PATH_LINE_ID_SECTION = PATH_LINE + "/{lineId}/section";

    public static void 등록이_불가하다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 기존_구간_뒤에_하행_종점으로_등록한_중앙역_구간이_함께_조회됨(String pathVariable, String... containValues) {
        JsonPath 목록조회 = 목록조회(PATH_LINE_ID_SECTION, pathVariable);
        assertThat(목록조회.getList("distances", String.class)).containsExactly(containValues);
        assertThat(목록조회.getList("sortNos", String.class)).contains("경기 광주역", "모란역", "중앙역");
    }


    public static void 기존_구간_앞에_상행_종점으로_등록한_모란역_구간이_함께_조회됨(String pathVariable, String... containValues) {
        JsonPath 목록조회 = 목록조회(PATH_LINE_ID_SECTION, pathVariable);
        assertThat(목록조회.getList("distances", String.class)).containsExactly(containValues);
        assertThat(목록조회.getList("sortNos", String.class)).containsExactly("모란역", "경기 광주역", "중앙역");
    }

    public static void 새로운_길이를_뺀_나머지를_새롭게_추가된_역과의_길이로_설정(String pathVariable, String... containValues) {
        JsonPath 목록조회 = 목록조회(PATH_LINE_ID_SECTION, pathVariable);
        assertThat(목록조회.getList("distances", String.class)).containsExactly(containValues);
        assertThat(목록조회.getList("sortNos", String.class)).contains("경기 광주역", "모란역", "중앙역");
    }

    public static ExtractableResponse<Response> 새로운_역_하행_종점으로_등록(String upStationId, String downStationId,
                                                                 String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 새로운_역_상행_종점으로_등록(String upStationId, String downStationId,
                                                                 String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 기존노선과_동일하게_상행_하행역을_등록(String upStationId, String downStationId,
                                                                      String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 기존노선의_상행_하행_역과_모두_일치하지_않게_등록(String upStationId, String downStationId,
                                                                             String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 기존역_구간_길이보다_크거나_같은_역을_기존역_사이_등록(String upStationId,
                                                                                String downStationId, String distance,
                                                                                String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 역_사이_새로운역_등록(String upStationId, String downStationId, String distance,
                                                             String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static String 지하철_노선_등록되어_있음(String name, String color, String upStationId, String downStationId,
                                        String distance, String returnValue) {
        return 생성_값_리턴(노선(name, color, upStationId, downStationId, distance), PATH_LINE, returnValue);
    }

    private static JsonPath 목록조회(String path, String pathVariable) {
        return RestAssured.given().log().all()
                .when().get(path, pathVariable)
                .then().log().all()
                .extract().jsonPath();
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
