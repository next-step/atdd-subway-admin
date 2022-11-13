package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.fixtures.TestFixtures;

class LineTestFixtures extends TestFixtures {

    private static final String PATH = "/lines";

    protected ExtractableResponse<Response> 노선_생성(String name, String color, String upStationId, String downStationId,
                                                  String distance) {
        return 생성(노선(name, color, upStationId, downStationId, distance), PATH);
    }

    protected List<String> 노선_목록조회(String information) {
        return 목록조회(information, PATH);
    }

    protected ExtractableResponse<Response> 노선_수정(String name, String color, String upStationId, String downStationId,
                                                  String distance, String path, String pathVariable) {
        return 수정(노선(name, color, upStationId, downStationId, distance), PATH + path, pathVariable);
    }

    protected String 노선_조회(String path, String pathVariable, String information) {
        return RestAssured.given().log().all()
                .when().get(PATH + path, pathVariable)
                .then().log().all()
                .extract().jsonPath().getString(information);
    }

    protected String 노선_생성_값_리턴(String name, String color, String upStationId, String downStationId,
                                String distance, String value) {
        return 생성_값_리턴(노선(name, color, upStationId, downStationId, distance), PATH, value);
    }

    protected ExtractableResponse<Response> 노선_삭제(String path, String pathVariable) {
        return 삭제(PATH + path, pathVariable);
    }

    private Map<String, String> 노선(String name, String color, String upStationId, String downStationId,
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
