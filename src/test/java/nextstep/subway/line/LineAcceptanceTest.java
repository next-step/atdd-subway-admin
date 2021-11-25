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

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_종점역_추가하여_생성_요청("강남역", "역삼역", "2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_종점역_추가하여_생성_요청("구로역", "신도림역", "1호선", "blue");
        지하철_노선_종점역_추가하여_생성_요청("강남역", "역삼역", "2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(Arrays.asList("1호선", "2호선"), response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> saveLineResponse = 지하철_노선_종점역_추가하여_생성_요청("강남역", "역삼역", "2호선", "green");
        LineResponse savedLineResponse = 저장된_노선_응답(saveLineResponse);

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
        ExtractableResponse<Response> saveLineResponse = 지하철_노선_종점역_추가하여_생성_요청("강남역", "역삼역", "2호선", "green");
        LineResponse savedLineResponse = 저장된_노선_응답(saveLineResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(savedLineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    @DisplayName("노선 생성시 두 종점역 추가하기")
    @Test
    void createLineWithStations() {
        // given, when
        ExtractableResponse<Response> saveLineResponse = 지하철_노선_종점역_추가하여_생성_요청("강남역", "역삼역", "2호선", "green");

        // then
        지하철_노선_생성_응답됨(saveLineResponse);
    }

    @DisplayName("노선 조회, 역 목록 추가")
    @Test
    void findLineWithStations() {
        // given
        LineResponse saveLineResponse = 지하철_노선_종점역_추가하여_생성_요청("강남역", "역삼역", "2호선", "green").body()
                .as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(saveLineResponse.getId());

        // then
        지하철_노선_역_목록_응답됨(Arrays.asList(saveLineResponse.getStations().get(0).getId(), saveLineResponse.getStations().get(1).getId()), response);
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

    private ExtractableResponse<Response> 지하철_노선_종점역_추가하여_생성_요청(String upStationName, String downStationName, String name, String color) {
        StationResponse upStation = 역_생성(upStationName);
        StationResponse downStation = 역_생성(downStationName);

        LineRequest lineRequestWithStations = new LineRequest(name, color, upStation.getId(), downStation.getId(), 10);
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