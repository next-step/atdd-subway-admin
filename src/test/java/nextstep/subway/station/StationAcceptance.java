package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

public class StationAcceptance {

    /**
     * 전달받은 지하철역 목록을 저장한다
     * @param name 지하철역 이름 목록
     */
    public static ExtractableResponse<Response> 지하철역_생성(final String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    /**
     * 지하철역 목록을 조회한다
     */
    public static ExtractableResponse<Response> 지하철역_조회() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    public static List<String> toStationNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    public static Long toStationId(ExtractableResponse<Response> response) {
        return response.as(StationResponse.class).getId();
    }
}
