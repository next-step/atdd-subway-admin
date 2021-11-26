package nextstep.subway.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.LineFixture.팔호선;
import static nextstep.subway.fixture.StationFixture.역_생성_응답;

public class HttpMethod {
    public static ExtractableResponse<Response> 지하철_노선_등록(Line line, Map<String, String> upStation, Map<String, String> downStation, int distance) {
        StationResponse upStationResponse = 역_생성_응답.get(upStation.get("name"));
        StationResponse downStationResponse = 역_생성_응답.get(downStation.get("name"));

        Map<String, String> params = new HashMap<String, String>() {{
            put("name", line.getName());
            put("color", line.getColor());
            put("upStationId", String.valueOf(upStationResponse.getId()));
            put("downStationId", String.valueOf(downStationResponse.getId()));
            put("distance", String.valueOf(distance));
        }};
        return 등록요청(params, "/lines");
    }

    public static ExtractableResponse<Response> 지하철_구간_등록(String lindId, Map<String, String> params) {
        return 등록요청(params, "/lines/" + lindId + "/sections");
    }

    public static ExtractableResponse<Response> 등록요청(Map<String, String> params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(ExtractableResponse response) {
        return RestAssured.given().log().all()
            .when()
            .get(response.header("Location"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(ExtractableResponse response, LineRequest params) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .put(response.header("Location"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거(ExtractableResponse response) {
        return RestAssured.given().log().all()
            .when()
            .delete(response.header("Location"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_등록(Map<String, String> params) {
        return 등록요청(params, "/stations");
    }

    public static ExtractableResponse<Response> 팔호선_노선_등록(Map<String, String> upStation, Map<String, String> downStation, int distance) {
        return 지하철_노선_등록(팔호선, upStation, downStation, distance);
    }

    public static ExtractableResponse<Response> 팔호선_구간_등록(ExtractableResponse<Response> response, Map<String, String> upStation, Map<String, String> downStation, int distance) {
        Long lineId = response.as(LineResponse.class).getId();
        HashMap<String, String> param = new HashMap<String, String>() {{
            put("upStationId", String.valueOf(역_생성_응답.get(upStation.get("name")).getId()));
            put("downStationId", String.valueOf(역_생성_응답.get(downStation.get("name")).getId()));
            put("distance", String.valueOf(distance));
        }};
        return 지하철_구간_등록(String.valueOf(lineId), param);
    }

    public static ExtractableResponse<Response> 상행역_기반_구간_제거(ExtractableResponse<Response> response, Long stationId) {
        Long lineId = response.as(LineResponse.class).getId();
        return 지하철_구간_제거(lineId, stationId);
    }

    public static ExtractableResponse<Response> 지하철_구간_제거(Long lineId, Long stationId) {
        return 구간제거요청(lineId, stationId);
    }

    public static ExtractableResponse<Response> 구간제거요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
            .param("stationId", stationId)
            .when()
            .delete("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }
}
