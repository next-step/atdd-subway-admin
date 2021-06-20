package nextstep.subway.station;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;

public class StationAcceptanceBefore {

    public static final String RESOURCES = "/stations";

    public static ExtractableResponse<Response> requestCreateStation(Long id, String name) {
        return RestAssured.given().log().all()
            .body(StationRequest.of(id, name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(RESOURCES)
            .then().log().all()
            .extract();
    }

}
