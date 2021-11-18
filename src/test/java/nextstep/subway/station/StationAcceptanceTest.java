package nextstep.subway.station;

import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역_생성_요청값());

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(강남역_생성_요청값());

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역_생성_요청값());

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_응답됨(response);
        지하철_역_목록_포함됨(response, 강남역_생성_응답, 역삼역_생성_응답);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(강남역_생성_응답.getId());

        // then
        지하철_역_삭제됨(response);
    }

    @DisplayName("생성되지 않은 지하철역을 제거한다.")
    @Test
    void deleteNotCreatedStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(UNKNOWN_STATION_ID);

        // then
        지하철_역_찾지_못함(response);
    }

    private static ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static StationResponse 지하철_역_등록되어_있음(StationRequest stationRequest) {
        ExtractableResponse<Response> response = 지하철_역_생성_요청(stationRequest);
        return response.jsonPath().getObject(".", StationResponse.class);
    }

    private void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    private void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, StationResponse... createStationResponses) {
        List<Long> expectedIds = Stream.of(createStationResponses)
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        List<Long> actualIds = response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualIds).containsAll(expectedIds);
    }

    private ExtractableResponse<Response> 지하철_역_제거_요청(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/stations/{stationId}", id)
            .then().log().all()
            .extract();
        return response;
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_역_찾지_못함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
