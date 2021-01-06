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
 * @since : 2021-01-01
 */
@SuppressWarnings("NonAsciiCharacters")
public class SectionRestHelper {

    public static ExtractableResponse<Response> 지하철_구간_생성(
            Long lineId, Station upStation, Station downStation, long distance
    ) {
        return RestAssured.given().log().all()
                .body(lineStationParamsGenerator(upStation.getId(), downStation.getId(), distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/"+ lineId +"/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제(Long lineId, Long stationId) {
        Map<String, String> params = new HashMap<>();
        params.put("stationId", String.valueOf(stationId));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/"+ lineId +"/sections")
                .then().log().all()
                .extract();
    }

    public static Map<String, String> lineStationParamsGenerator(
            Long upStationId, Long downStationId, long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", String.valueOf(distance));
        return params;
    }

}
