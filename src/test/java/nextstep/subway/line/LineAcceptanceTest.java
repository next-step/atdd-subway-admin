package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.station.TestStationFactory.역_생성;
import static nextstep.subway.utils.TestGetRequestFactory.요청_get;
import static nextstep.subway.utils.TestPostRequestFactory.요청_post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    String DEFAULT_PATH = "/lines";
    LineRequest lineRequest = new LineRequest("2호선", "green");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_응답됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청(lineRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest secondLine = new LineRequest("2호선", "green");
        LineRequest thirdLine = new LineRequest("3호선", "orange");
        지하철_노선_생성_요청(secondLine);
        지하철_노선_생성_요청(thirdLine);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(Arrays.asList(secondLine.getName(), thirdLine.getName()), response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> saveResponse = 지하철_노선_생성_요청(lineRequest);
        LineResponse lineResponse = 저장된_노선_응답(saveResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        지하철_노선_조회_응답됨(lineResponse, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> saveResponse = 지하철_노선_생성_요청(lineRequest);
        LineResponse savedLineResponse = 저장된_노선_응답(saveResponse);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(savedLineResponse);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(savedLineResponse, response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> saveResponse = 지하철_노선_생성_요청(lineRequest);
        LineResponse savedLineResponse = 저장된_노선_응답(saveResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(savedLineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    @DisplayName("노선 생성시 두 종점역 추가하기")
    @Test
    void createLineWithStations() {
        // given
        StationResponse stationResponse1 = 역_생성("강남역");
        StationResponse stationResponse2 = 역_생성("역삼역");

        // when
        ExtractableResponse<Response> saveResponse = 지하철_노선_종점역_추가하여_생성_요청(stationResponse1.getId(), stationResponse2.getId());

        // then
        지하철_노선_생성_응답됨(saveResponse);
    }

    @DisplayName("노선 조회, 역 목록 추가")
    @Test
    void findLineWithStations() {
        // given
        StationResponse stationResponse1 = 역_생성("강남역");
        StationResponse stationResponse2 = 역_생성("역삼역");
        LineResponse lineResponse = 지하철_노선_종점역_추가하여_생성_요청(stationResponse1.getId(), stationResponse2.getId()).body()
                .as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        지하철_노선_역_목록_응답됨(Arrays.asList(stationResponse1.getId(), stationResponse2.getId()), response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return 요청_post(DEFAULT_PATH, lineRequest);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return 요청_get(DEFAULT_PATH);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return 요청_get(DEFAULT_PATH + "/" + id);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(LineResponse savedLineResponse) {
        LineRequest updateLineRequest = new LineRequest(savedLineResponse.getName(), "red");

        return RestAssured
                .given().log().all()
                .body(updateLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + savedLineResponse.getId())
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_종점역_추가하여_생성_요청(Long stationId1, Long stationId2) {
        LineRequest lineRequestWithStations = new LineRequest("2호선", "green", stationId1, stationId2, 10);
        return 지하철_노선_생성_요청(lineRequestWithStations);
    }

    private void 지하철_노선_생성_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private LineResponse 저장된_노선_응답(ExtractableResponse<Response> saveResponse) {
        return saveResponse.body().as(LineResponse.class);
    }

    private void 지하철_노선_목록_응답됨(List<String> lineRequestNames, ExtractableResponse<Response> response) {
        List<String> lineResponseNames = response.jsonPath().getList(".", LineResponse.class)
                .stream()
                .map(lineResponse -> lineResponse.getName())
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponseNames).containsAll(lineRequestNames)
        );
    }

    private void 지하철_노선_조회_응답됨(LineResponse lineResponse, ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getLong("id")).isEqualTo(lineResponse.getId())
        );
    }

    private void 지하철_노선_수정됨(LineResponse savedLineResponse, ExtractableResponse<Response> response) {
        LineResponse updatedLineResponse = response.body().as(LineResponse.class);
        assertAll(
                () -> assertThat(updatedLineResponse.getId()).isEqualTo(savedLineResponse.getId()),
                () -> assertThat(updatedLineResponse.getColor()).isEqualTo("red")
        );
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_역_목록_응답됨(List<Long> stationIds, ExtractableResponse<Response> response) {
        List<Long> lineResponseStationIds = response.body()
                .as(LineResponse.class)
                .getStations()
                .stream()
                .map(stationResponse -> stationResponse.getId())
                .collect(Collectors.toList());

        assertThat(lineResponseStationIds).containsAll(stationIds);
    }
}