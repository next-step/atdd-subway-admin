package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionTestFixture {
    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", Integer.toString(distance));

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
