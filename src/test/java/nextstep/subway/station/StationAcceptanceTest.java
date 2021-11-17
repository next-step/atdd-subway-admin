package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.exception.ApiErrorMessage;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
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
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.as(ApiErrorMessage.class).getMessage())
                        .isEqualTo("고유 인덱스 또는 기본 키 위반입니다.")
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> stationResponse = 지하철_역_생성_요청(stationRequest);

        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> stationResponse2 = 지하철_역_생성_요청(stationRequest2);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> {
                    List<Long> expectedLineIds = Arrays.asList(stationResponse, stationResponse2)
                            .stream()
                            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                            .collect(Collectors.toList());
                    List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class)
                            .stream()
                            .map(it -> it.getId())
                            .collect(Collectors.toList());
                    assertThat(resultLineIds).containsAll(expectedLineIds);
                }
        );
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

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = 지하철_역_생성_요청(stationRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
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
}
