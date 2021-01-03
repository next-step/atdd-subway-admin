package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.List;

public class StationAcceptanceTestRequest {
    public static StationResponse 지하철역_생성_요청(String stationName) {
        StationRequest request = createStationRequest(stationName);
        return 지하철역_생성_요청(request)
                .body().as(StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청후_응답(String stationName) {
        return 지하철역_생성_요청(createStationRequest(stationName));
    }

    public static ExtractableResponse<Response> 지하철역_재생성_요청(String stationName) {
        StationRequest request = createStationRequest(stationName);
        return 지하철역_생성_요청(request);
    }

    public static List<StationResponse> 지하철역_모두_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract()
                .jsonPath().getList(".", StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(ExtractableResponse<Response> response) {
        return RestAssured.given().log().all()
                .when()
                .delete(extractLocation(response))
                .then().log().all()
                .extract();
    }

    private static String extractLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    private static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();
    }

    private static StationRequest createStationRequest(String name) {
        return new StationRequest(name);
    }
}
