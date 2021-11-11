package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineSteps {

    public static final String LINE_URI = "/lines";

    public static LineResponse 지하철_노선_등록되어_있음(String name, String color) {
        return 지하철_노선_생성_요청(name, color).as(LineResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        return RestAssured
                .given().log().all()
                .body(getLineParams(name, color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get(LINE_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse 이호선) {
        return RestAssured
                .given().log().all()
                .when().get(LINE_URI + "/{id}", 이호선.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, String name, String color) {
        return RestAssured
                .given().log().all()
                .body(getLineParams(name, color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(LINE_URI + "/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete(LINE_URI + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private static Map<String, String> getLineParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }
}
