package nextstep.subway.station;

import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredBuilder;
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
class StationAcceptanceTest extends AcceptanceTest {

    public static final String BASE_STATION_URL = "/stations";

    private StationRequest 강남역_요청_데이터() {
        return new StationRequest("강남역");
    }

    private StationRequest 역삼역_요청_데이터() {
        return new StationRequest("역삼역");
    }

    private ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
        return new RestAssuredBuilder(Method.POST, BASE_STATION_URL)
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .setBody(stationRequest)
                .build();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return new RestAssuredBuilder(Method.GET, BASE_STATION_URL)
                .build();
    }

    private ExtractableResponse<Response> 지하철역_제거_요청(StationResponse stationResponse) {
        return new RestAssuredBuilder(Method.DELETE, BASE_STATION_URL + "/" + stationResponse.getId())
                .build();
    }

    private StationResponse 지하철역_등록되어_있음(StationRequest stationRequest) {
        return 지하철역_생성_요청(stationRequest)
                .as(StationResponse.class);
    }

    private void 지하철역_조회_데이터_확인(ExtractableResponse<Response> response, StationResponse... stationResponses) {
        List<Long> expectedStationIds = Arrays.stream(stationResponses)
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath()
                .getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds)
                .containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = 강남역_요청_데이터();

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(stationRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location"))
                        .isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청(강남역_요청_데이터());

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역_요청_데이터());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationResponse 강남역 = 지하철역_등록되어_있음(강남역_요청_데이터());
        StationResponse 역삼역 = 지하철역_등록되어_있음(역삼역_요청_데이터());

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value()),
                () -> 지하철역_조회_데이터_확인(response, 강남역, 역삼역)
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse 강남역 = 지하철역_등록되어_있음(강남역_요청_데이터());

        // when
        ExtractableResponse<Response> response = 지하철역_제거_요청(강남역);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
