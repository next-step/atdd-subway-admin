package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestStationAcceptanceFactory {

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_생성_요청(String name) {
        StationRequest stationRequest = 지하철_역_파라미터_생성(name);

        return RestAssured
                .given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_삭제_요청(Long deleteStationId) {
        return RestAssured
                .given().log().all()
                .when().delete("/stations/{id}", deleteStationId)
                .then().log().all()
                .extract();
    }

    public static StationRequest 지하철_역_파라미터_생성(String name) {
        return StationRequest.from(name);
    }

    public static List<Long> 지하철_역_목록_ID_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    @SafeVarargs
    public static final List<Long> 지하철_역_ID_추출(ExtractableResponse<Response>... createResponse) {
        return Arrays.stream(createResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }
}
