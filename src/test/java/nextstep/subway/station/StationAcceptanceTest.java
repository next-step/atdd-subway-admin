package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.exception.ApiErrorMessage;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.StationResponseList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(stationRequest);

        // then
        지하철_역_생성됨(response);
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        StationRequest stationRequest = new StationRequest("강남역");

        // given
        지하철_역_등록되어_있음(stationRequest);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(stationRequest);

        // then
        지하철_역_생성_실패됨(response);
    }

    private void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ApiErrorMessage.class).getMessage())
                        .isEqualTo("중복된 역을 추가할 수 없습니다.")
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest stationRequest = new StationRequest("강남역");
        StationResponse stationResponse = 지하철_역_등록되어_있음(stationRequest);

        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationResponse stationResponse2 = 지하철_역_등록되어_있음(stationRequest2);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_응답됨(response);
        지하철_역_목록에_포함_검증(response, stationResponse, stationResponse2);
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given()
                .log().all()
                .when()
                .get("/stations")
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_역_목록에_포함_검증(final ExtractableResponse<Response> response, final StationResponse... stationResponses) {
        StationResponseList stationResponseList = response.as(StationResponseList.class);

        assertAll(
                () -> assertThat(stationResponseList).isNotNull(),
                () -> {
                    List<StationResponse> createdLines = Arrays.stream(stationResponses).collect(Collectors.toList());
                    assertThat(stationResponseList.getStationResponseList()).containsAll(createdLines);
                }
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = 지하철_역_생성_요청(stationRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_역_제거_요청(uri);

        // then
        지하철_역_삭제됨(response);
    }

    private StationResponse 지하철_역_등록되어_있음(final StationRequest request) {
        ExtractableResponse<Response> response = 지하철_역_생성_요청(request);
        return response.as(StationResponse.class);
    }

    private ExtractableResponse<Response> 지하철_역_생성_요청(final StationRequest stationRequest) {
        return RestAssured.given()
                .log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_제거_요청(final String uri) {
        return RestAssured.given()
                .log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
