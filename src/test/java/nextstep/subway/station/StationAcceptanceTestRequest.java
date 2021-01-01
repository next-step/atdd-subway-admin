package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import org.springframework.http.MediaType;

public class StationAcceptanceTestRequest {
    public static long 지하철역_생성_요청후_Id(String stationName) {
        ExtractableResponse<Response> response = 지하철역_생성_요청(stationName);
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(String stationName) {
        StationRequest request = createStationRequest(stationName);
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
