package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
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

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private static final String BASE_URI = "/stations";
    private final StationRequest 강남역 = StationRequest.of("강남역");
    private final StationRequest 역삼역 = StationRequest.of("역삼역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성됨(response);
    }


    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);
        Long 역삼역_ID = 지하철_역_등록되어_있음(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_응답됨(response);
        지하철_역_목록_포함됨(response, Arrays.asList(강남역_ID, 역삼역_ID));
    }


    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Long 강남역_ID = 지하철_역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(강남역_ID);

        // then
        지하철_역_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_역_생성_요청(StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(BASE_URI)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get(BASE_URI)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_제거_요청(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete(BASE_URI + "/{id}", id)
                .then().log().all()
                .extract();
    }

    private Long 지하철_역_등록되어_있음(StationRequest request) {
        ExtractableResponse<Response> response = 지하철_역_생성_요청(request);
        지하철_역_생성됨(response);
        return responseStation(response).getId();
    }

    private void 지하철_역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_역_목록_포함됨(ExtractableResponse<Response> response, List<Long> expectedIds) {
        List<Long> resultIds = responseStations(response).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(expectedIds).containsAll(resultIds);
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private StationResponse responseStation(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("", StationResponse.class);
    }

    private List<StationResponse> responseStations(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class);
    }
}
