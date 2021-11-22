package nextstep.subway.station;

import io.restassured.RestAssured;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

public class TestStationFactory {

    public static StationResponse 역_미리_생성(String name) {
        StationRequest stationRequest = new StationRequest(name);

        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract()
                .body()
                .as(StationResponse.class);
    }
}
