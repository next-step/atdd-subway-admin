package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

public class StationSteps {

    private static final String STATIONS_URI = "/stations";

    public static StationResponse 지하철_역_등록되어_있음(StationRequest stationRequest) {
        return 지하철_역_생성_요청(stationRequest).as(StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATIONS_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get(STATIONS_URI)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete(STATIONS_URI + "/{id}", id)
                .then().log().all()
                .extract();
    }
}
