package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.station.domain.Station;
import org.springframework.http.MediaType;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-24
 */
@SuppressWarnings("NonAsciiCharacters")
public class LineRestHelper {

    public static ExtractableResponse<Response> 지하철_라인_생성(
            String color, String name,
            Station upStation, Station downStation, long distance
    ) {
        return RestAssured.given().log().all()
                .body(lineParamsGenerator(color, name, upStation.getId(), downStation.getId(), distance))
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

    public static ExtractableResponse<Response> 지하철_라인_수정(Long id, String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

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

    public static Map<String, String> lineParamsGenerator(
            String color, String name,
            Long upStationId, Long downStationId, long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", String.valueOf(distance));
        return params;
    }

}
