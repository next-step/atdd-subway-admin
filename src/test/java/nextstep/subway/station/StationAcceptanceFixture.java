package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StationAcceptanceFixture {
    private StationAcceptanceFixture() {
    }

    private static Map<String, String> createParams(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }

    public static ExtractableResponse<Response> 지하철역_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_생성을_요청한다(String name) {
        Map<String, String> params = createParams(name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제를_요청한다(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static List<Long> ofStationResponseIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    public static StationResponse ofStationResponse(ExtractableResponse<Response> response) {
        return response.as(StationResponse.class);
    }

    public static List<StationResponse> ofStationResponses(StationResponse... stations) {
        return Arrays.stream(stations)
                .collect(Collectors.toList());
    }
}
