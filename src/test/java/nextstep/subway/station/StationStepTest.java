package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.StationsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.AcceptanceTest.extractIdByLocationHeader;
import static org.assertj.core.api.Assertions.assertThat;

public class StationStepTest {

    public static final String STATION_BASE_URL = "/stations";

    static void 지하철_역_조회_성공됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, Long...ids) {
        StationsResponse stations = response.body().as(StationsResponse.class);

        List<Long> resultIds = stations.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultIds).containsAll(List.of(ids));
    }

    static void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    static ExtractableResponse<Response> 지하철_역_삭제_요청(long createdId) {
        return RestAssured.given().log().all()
                .when()
                .delete(STATION_BASE_URL + "/" + createdId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get(STATION_BASE_URL)
                .then().log().all()
                .extract();
    }

    static void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static long 지하철_역_등록되어_있음(StationRequest request) {
        ExtractableResponse<Response> createdStation = 지하철_역_생성_요청(request);
        return extractIdByLocationHeader(createdStation);
    }

    static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(STATION_BASE_URL)
                .then().log().all()
                .extract();
    }
}
