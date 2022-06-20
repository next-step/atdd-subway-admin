package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.StationRequest;
import org.springframework.http.MediaType;

public class StationTestHelper {
    private StationTestHelper() {
    }


    public static Long 지하철역_생성하고_역_ID_응답(String name) {
        return StationTestHelper.지하철역_생성(name)
                .jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철역_생성(String name) {
        StationRequest stationRequest = new StationRequest(name);
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_전체목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_ID로_삭제(Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/stations/{stationId}", stationId)
                .then().log().all()
                .extract();
    }
}
