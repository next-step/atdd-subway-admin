package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.MediaType;

public class LineTestUtil {

    private LineTestUtil() {
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String lineName) {
        Map<String, String> params = 지하철_노선_생성_파라미터_맵핑(lineName);
        return 지하철_노선_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청(String url) {
        return RestAssured.given().log().all()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(
        Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> updateParams,
        long lineId) {
        return RestAssured.given().log().all()
            .body(updateParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .patch("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(
        ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then()
            .log().all()
            .extract();
    }

    public static Map<String, String> 지하철_노선_생성_파라미터_맵핑(String s) {
        Map<String, String> params = new HashMap<>();
        params.put("name", s);
        params.put("color", "orange darken-4");
        return params;
    }

    public static List<Long> ids_추출_By_LineResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
    }

    public static List<Long> ids_추출_By_Location(
        List<ExtractableResponse<Response>> createResponses) {
        return createResponses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

}
