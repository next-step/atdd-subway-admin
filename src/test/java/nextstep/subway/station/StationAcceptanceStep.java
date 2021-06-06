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
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

public class StationAcceptanceStep {
    private static final String STATION_BASE_PATH = "/stations";

    public static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(STATION_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get(STATION_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_제거_요청(Long stationId) {
        return RestAssured.given().log().all()
            .when()
            .delete(STATION_BASE_PATH + "/" + stationId)
            .then().log().all()
            .extract();
    }

    public static Long 지하철_역_등록되어_있음(StationRequest request) {
        ExtractableResponse<Response> createdResponse = 지하철_역_생성_요청(request);
        return createdResponse.as(LineResponse.class).getId();
    }

    public static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_역_목록_포함됨(Long stationId1,
        Long stationId2,
        ExtractableResponse<Response> response) {
        List<Long> expectedStationIds = Arrays.asList(stationId1, stationId2);
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    public static void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
