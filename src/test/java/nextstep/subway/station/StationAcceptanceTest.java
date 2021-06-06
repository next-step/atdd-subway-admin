package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        ExtractableResponse<Response> response = 지하철_역_생성_요청("강남역");

        // then
        지하철_역_생성됨(response);
        지하철_역_조회_URL_확인됨(response);
    }

    @Test
    @DisplayName("지하철 역 목록을 한번에 생성한다.")
    void createStationList() {
        // given
        List<StationRequest> stationRequests = Arrays.asList(new StationRequest("강남역"),
                new StationRequest("역삼역"));

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_생성_요청(stationRequests);

        // then
        지하철_역_목록_생성됨(response);
    }

    @Test
    @DisplayName("지하철 역 목록 생성 시 중복 예외")
    void createAllStations_duplicate_exception() {
        // given
        List<StationRequest> stationRequests = Arrays.asList(new StationRequest("강남역"),
                new StationRequest("강남역"));

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_생성_요청(stationRequests);

        // then
        지하철_역_목록_생성_실패됨(response);
        지하철_역_요청에러_메시지확인(response, "중복된 지하철 역 이름은 등록될 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청("강남역");

        // then
        지하철_역_등록_실패됨(response);
        지하철_역_요청에러_메시지확인(response, "역 생성에 실패했습니다. 이미 존재하는 역입니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        List<StationResponse> stationsResponses = 지하철_역_목록_등록되어_있음(Arrays.asList("강남역", "역삼역"));

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_응답됨(response);
        지하철_역_목록_포함됨(stationsResponses, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationResponse 강남역 = 지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(강남역.getId());

        // then
        지하철_역_삭제됨(response);
    }

    @Test
    void delete_failed_exception() {
        // given
        StationResponse 강남역 = 지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_삭제_요청(강남역.getId() + 100L);

        // then
        지하철_역_삭제_실패됨(response);
        지하철_역_요청에러_메시지확인(response, "삭제 대상 역이 존재하지 않습니다.");
    }

    private ExtractableResponse<Response> 지하철_역_생성_요청(String stationName) {
        return RestAssured.given().log().all()
                .body(new StationRequest(stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_목록_생성_요청(List<StationRequest> stationRequests) {
        return RestAssured.given().log().all()
                .body(stationRequests)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations/all")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + id)
                .then().log().all()
                .extract();
    }

    public StationResponse 지하철_역_등록되어_있음(String stationName) {
        return 지하철_역_생성_요청(stationName)
                .jsonPath()
                .getObject(".", StationResponse.class);
    }

    public List<StationResponse> 지하철_역_목록_등록되어_있음(List<String> stationNames) {
        List<StationRequest> stationRequests = stationNames.stream()
                .map(name -> new StationRequest(name))
                .collect(Collectors.toList());

        return 지하철_역_목록_생성_요청(stationRequests)
                .jsonPath()
                .getList(".", StationResponse.class);
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_역_목록_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_역_조회_URL_확인됨(ExtractableResponse<Response> response) {
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_역_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_역_목록_포함됨(List<StationResponse> stationResponses, ExtractableResponse<Response> response) {
        List<Long> stationIds = stationResponses.stream().map(res -> res.getId()).collect(Collectors.toList());
        List<Long> responseStationIds = response.jsonPath().getList("id", Long.class);
        assertThat(stationIds).containsAll(responseStationIds);
    }

    private void 지하철_역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_역_요청에러_메시지확인(ExtractableResponse<Response> response, String errorMessage) {
        String responseErrorMessage = response.jsonPath().getObject("errorMessage", String.class);
        assertThat(responseErrorMessage).isEqualTo(errorMessage);
    }

    private void 지하철_역_목록_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_역_삭제_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
