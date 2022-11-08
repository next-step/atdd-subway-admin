package nextstep.subway.station;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import org.apache.groovy.util.Maps;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationAcceptanceTestAssured {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String REQUEST_PATH = "/stations";

    static Long 지하철역_식별자(ExtractableResponse<Response> 지하철역_생성_응답) {
        String 응답본문 = 지하철역_생성_응답.response().getBody().print();
        return JsonPath.from(응답본문).getLong(ID);
    }

    static Map<String, String> 지하철역_요청_파라미터(String 지하철역_이름) {
        return Maps.of(NAME, 지하철역_이름);
    }

    static List<String> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
            .when().get(REQUEST_PATH)
            .then().log().all()
            .extract().jsonPath().getList(NAME, String.class);
    }

    static ExtractableResponse<Response> 지하철역_생성(Map<String, String> 요청_파라미터) {
        return RestAssured.given().log().all()
            .body(요청_파라미터)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(REQUEST_PATH)
            .then().log().all()
            .extract();
    }

    static ExtractableResponse<Response> 지하철역_생성(String 지하철역_이름) {
        return 지하철역_생성(지하철역_요청_파라미터(지하철역_이름));
    }

    static void 지하철역_삭제(Long id) {
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(format("%s/%s", REQUEST_PATH, id))
            .then().log().all()
            .extract();
    }

}
