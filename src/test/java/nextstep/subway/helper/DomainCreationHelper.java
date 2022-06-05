package nextstep.subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class DomainCreationHelper {
    private DomainCreationHelper() {
    }

    public static ExtractableResponse<Response> 지하철역_생성됨(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_생성됨(
            final String name,
            final String color,
            final Long upStationId,
            final Long downStationId
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", 7);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_생성됨(
            final String name,
            final String color,
            final Long upStationId,
            final Long downStationId,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_상행_종점_생성됨(
            final Long lineId,
            final String stationName,
            final Long lineUpStationId,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 지하철역_생성됨(stationName).jsonPath().getLong("id"));
        params.put("downStationId", lineUpStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_하행_기점_생성됨(
            final Long lineId,
            final Long lineDownStationId,
            final String stationName,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", lineDownStationId);
        params.put("downStationId", 지하철역_생성됨(stationName).jsonPath().getLong("id"));
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_상행_기점_생성됨(
            final Long lineId,
            final Long lineUpStationId,
            final String stationName,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", lineUpStationId);
        params.put("downStationId", 지하철역_생성됨(stationName).jsonPath().getLong("id"));
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_중간역_상행역_교체_생성됨(
            final Long lineId,
            final Long lineUpStationId,
            final String stationName,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", lineUpStationId);
        params.put("downStationId", 지하철역_생성됨(stationName).jsonPath().getLong("id"));
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_중간역_하행역_교체_생성됨(
            final Long lineId,
            final String stationName,
            final Long lineDownStationId,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 지하철역_생성됨(stationName).jsonPath().getLong("id"));
        params.put("downStationId", lineDownStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_하행_종점_생성됨(
            final Long lineId,
            final String stationName,
            final Long lineDownStationId,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 지하철역_생성됨(stationName).jsonPath().getLong("id"));
        params.put("downStationId", lineDownStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_이미_둘다_등록된_노선_요청(
            final Long lineId,
            final Long lineUpStationId,
            final Long lineDownStationId,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", lineUpStationId);
        params.put("downStationId", lineDownStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철구간_상행_하행_새로_생성됨(
            final Long lineId,
            final String upStationName,
            final String downStationName,
            final Long distance
    ) {
        final Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 지하철역_생성됨(upStationName).jsonPath().getLong("id"));
        params.put("downStationId", 지하철역_생성됨(downStationName).jsonPath().getLong("id"));
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
