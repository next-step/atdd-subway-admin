package nextstep.subway.station.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import org.springframework.http.MediaType;

public class StationAcceptanceStep {
    public static ExtractableResponse<Response> 지하철역_생성됨(StationRequest stationRequest) {
        return 지하철역_생성_요청(stationRequest);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all().
                body(stationRequest).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/stations").
                then().
                log().all().
                extract();
    }
}
