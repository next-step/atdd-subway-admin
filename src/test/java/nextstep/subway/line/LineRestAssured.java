package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineRestAssured {

    public static ExtractableResponse<Response> 역_생성_요청_01() {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "주안역");
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 역_생성_요청_02() {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "동암역");
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 역_생성_요청_03() {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "신림역");
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 역_생성_요청_04() {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "신대방역");
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청_01(ExtractableResponse<Response> stationCreateResponse01, ExtractableResponse<Response> stationCreateResponse02, int distance) {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "1호선");
                      put("color", "#0000FF");
                      put("upStationId", stationCreateResponse01.jsonPath().get("id").toString());
                      put("downStationId", stationCreateResponse02.jsonPath().get("id").toString());
                      put("distance", String.valueOf(distance));
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청_01(ExtractableResponse<Response> lineCreateResponse, int distance) {
        List<Integer> stationIds = lineCreateResponse.jsonPath().get("stations.id");

        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "1호선");
                      put("color", "#0000FF");
                      put("upStationId", String.valueOf(stationIds.get(0)));
                      put("downStationId", String.valueOf(stationIds.get(1)));
                      put("distance", String.valueOf(distance));
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청_02(ExtractableResponse<Response> stationCreateResponse01, ExtractableResponse<Response> stationCreateResponse02, int distance) {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "2호선");
                      put("color", "#0000FF");
                      put("upStationId", stationCreateResponse01.jsonPath().get("id").toString());
                      put("downStationId", stationCreateResponse02.jsonPath().get("id").toString());
                      put("distance", String.valueOf(distance));
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청(String url) {
        return RestAssured.given().log().all()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(String expectLineId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + expectLineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String updateLineId, Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + updateLineId)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Integer lineId, Integer upStationId, Integer downStationId, Long distance) {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                put("upStationId", upStationId + "");
                put("downStationId", downStationId + "");
                put("distance", distance + "");
            }})
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선의_지하철역_제거_요청(Integer lineId, Integer stationId) {
        return RestAssured.given().log().all()
            .queryParam("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }
}
