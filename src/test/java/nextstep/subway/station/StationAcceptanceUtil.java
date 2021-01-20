package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

public class StationAcceptanceUtil {
    private StationAcceptanceUtil() {
        throw new AssertionError();
    }

    public static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static Station 지하철역_등록되어_있음(StationRequest params) {
        ExtractableResponse<Response> response = 지하철_역_생성_요청(params);
        return response.jsonPath()
                .getObject(".", StationResponse.class)
                .toStation();
    }

}
