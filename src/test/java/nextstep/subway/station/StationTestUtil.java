package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

public class StationTestUtil {
    public static final String STATIONS_PATH = "/stations/";

    public static StationRequest 지하철역_정보(String name) {
        return new StationRequest(name);
    }

    public static ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(STATIONS_PATH)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get(STATIONS_PATH)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_제거_요청(Long id) {
        return RestAssured.given().log().all()
            .when()
            .delete(STATIONS_PATH + id)
            .then().log().all()
            .extract();
    }

    public static Long 지하철역_생성_및_아이디_반환(String name) {
        StationRequest stationRequest = 지하철역_정보(name);
        ExtractableResponse<Response> response = 지하철역_생성_요청(stationRequest);
        return response.jsonPath().getLong("id");
    }

    public static void 지하철역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철역_목록_조회됨(ExtractableResponse<Response> response,
        ExtractableResponse<Response> stationResponse1,
        ExtractableResponse<Response> stationResponse2) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Arrays.asList(stationResponse1, stationResponse2).stream()
            .map(it -> it.jsonPath().getLong("id"))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 지하철역_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
