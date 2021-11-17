package nextstep.subway.line;

import static nextstep.subway.line.LineFixture.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
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
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());

        // when
        LineRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        지하철_노선_생성됨(response);
        지하철_노선_응답_내_역_목록에_종점들이_등록됨(response, 강남역_생성_응답, 역삼역_생성_응답);
        지하철_노선_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private void 지하철_노선_응답_내_역_목록에_종점들이_등록됨(
        ExtractableResponse<Response> response,
        StationResponse... stationCreateResponses
    ) {
        List<Long> actualIds = response.jsonPath().getObject(".", LineResponse.class).getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        List<Long> expectIds = Arrays.stream(stationCreateResponses)
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualIds).containsAll(expectIds);
    }

    private void 지하철_노선_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(ExtractableResponse<Response> response) {
        지하철_노선_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(response.jsonPath().getObject(".", LineResponse.class));
    }

    private void 지하철_노선_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(LineResponse response) {
        // TODO : 현재 응답값으로는 정렬된지 어떻게 검증할 수 있을까?
    }

    @DisplayName("존재하지 않는 지하철 역을 종점으로 하는 노선을 생성한다.")
    @Test
    void createLineWithUnknownStationId() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());

        // when
        LineRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), UNKNOWN_STATION_ID, 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        대상을_찾지_못해_지하철_노선_생성_실패됨(response);
    }

    private void 대상을_찾지_못해_지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        잘못된_요청으로_지하철_노선_생성_실패됨(response);
    }

    private LineResponse 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private void 잘못된_요청으로_지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        StationResponse 사당역_생성_응답 = 지하철_역_등록되어_있음(사당역_생성_요청값());
        StationResponse 동작역_생성_응답 = 지하철_역_등록되어_있음(동작역_생성_요청값());
        LineRequest 노선_4호선_생성_요청값 = 노선_4호선_생성_요청값(사당역_생성_응답.getId(), 동작역_생성_응답.getId(), 30);
        LineResponse 노선_4호선_생성_응답 = 지하철_노선_등록되어_있음(노선_4호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, 노선_2호선_생성_응답, 노선_4호선_생성_응답);
        지하철_노선_목록_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(response);
    }

    private void 지하철_노선_목록_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(ExtractableResponse<Response> response) {
        List<LineResponse> lineResponses = new ArrayList<>(response.jsonPath().getList(".", LineResponse.class));

        for (LineResponse lineResponse : lineResponses) {
            지하철_노선_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(lineResponse);
        }
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, LineResponse... createLineResponses) {
        List<Long> expectedLineIds = Stream.of(createLineResponses)
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선_2호선_생성_응답.getId());

        // then
        지하철_노선_응답됨(response);
        지하철_노선_응답_내_역_목록에_종점들이_등록됨(response, 강남역_생성_응답, 역삼역_생성_응답);
        지하철_노선_응답_내_역_목록이_상행역부터_하행역_순으로_정렬됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/{lineId}", id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("생성되지 않은 지하철 노선을 조회한다.")
    @Test
    void getNotCreatedLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(UNKNOWN_LINE_ID);

        // then
        지하철_노선_찾지_못함(response);
    }

    private void 지하철_노선_찾지_못함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(노선_2호선_생성_응답.getId(), 노선_수정_요청값());

        // then
        지하철_노선_수정됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineUpdateRequest body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{lineId}", id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("생성되지 않은 지하철 노선을 수정한다.")
    @Test
    void updatedNotCreatedLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(UNKNOWN_LINE_ID, 노선_수정_요청값());

        // then
        지하철_노선_찾지_못함(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(노선_2호선_생성_응답.getId());

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/{lineId}", id)
            .then().log().all()
            .extract();
        return response;
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("생성되지 않은 지하철 노선을 제거한다.")
    @Test
    void deleteNotCreatedLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(UNKNOWN_LINE_ID);

        // then
        지하철_노선_찾지_못함(response);
    }
}
