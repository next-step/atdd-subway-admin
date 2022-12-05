package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.utils.AcceptanceTestCommon;
import org.springframework.http.MediaType;

class StationAcceptanceTestFixture extends AcceptanceTestCommon {

    /**
     * 지하철역 조회
     *
     * @return 지하철역 조회 Response
     */
    protected static ExtractableResponse<Response> 지하철역_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    /**
     * 지하철역 생성
     *
     * @param stationName 생성할 지하철역 이름
     * @return 생성된 지하철역 id
     */
    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

}
