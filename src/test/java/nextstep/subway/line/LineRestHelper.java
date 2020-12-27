package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-24
 */
@SuppressWarnings("NonAsciiCharacters")
public class LineRestHelper {

    public static ExtractableResponse<Response> 지하철_라인_생성(String color, String name) {
        return RestAssured.given().log().all()
                .body(lineParamsGenerator(color, name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_라인_전체_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_라인_조회(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_라인_수정(Long id, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_라인_삭제(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static Map<String, String> lineParamsGenerator(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        return params;
    }

}
