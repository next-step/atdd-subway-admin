package nextstep.subway.line;

import static nextstep.subway.line.LineFixture.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static nextstep.subway.station.StationFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionAddRequest;
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
        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        지하철_노선_생성됨(response);
        지하철_노선_응답_내_역_목록에_상행역_하행역_순으로_조회됨(response.as(LineResponse.class), 강남역_생성_응답, 역삼역_생성_응답);
    }

    @DisplayName("존재하지 않는 지하철 역을 종점으로 하는 노선을 생성한다.")
    @Test
    void createLineWithUnknownStationId() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());

        // when
        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), UNKNOWN_STATION_ID, 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        대상을_찾지_못해_지하철_노선_생성_실패됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(노선_2호선_생성_요청값);

        // then
        잘못된_요청으로_지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        StationResponse 사당역_생성_응답 = 지하철_역_등록되어_있음(사당역_생성_요청값());
        StationResponse 동작역_생성_응답 = 지하철_역_등록되어_있음(동작역_생성_요청값());
        LineCreateRequest 노선_4호선_생성_요청값 = 노선_4호선_생성_요청값(사당역_생성_응답.getId(), 동작역_생성_응답.getId(), 30);
        LineResponse 노선_4호선_생성_응답 = 지하철_노선_등록되어_있음(노선_4호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, 노선_2호선_생성_응답, 노선_4호선_생성_응답);
        지하철_노선_응답_내_역_목록에_상행역_하행역_순으로_조회됨(response.jsonPath().getList(".", LineResponse.class).get(0), 강남역_생성_응답, 역삼역_생성_응답);
        지하철_노선_응답_내_역_목록에_상행역_하행역_순으로_조회됨(response.jsonPath().getList(".", LineResponse.class).get(1), 사당역_생성_응답, 동작역_생성_응답);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선_2호선_생성_응답.getId());

        // then
        지하철_노선_응답됨(response);
        지하철_노선_응답_내_역_목록에_상행역_하행역_순으로_조회됨(response.as(LineResponse.class), 강남역_생성_응답, 역삼역_생성_응답);
    }

    @DisplayName("생성되지 않은 지하철 노선을 조회한다.")
    @Test
    void getNotCreatedLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(UNKNOWN_LINE_ID);

        // then
        지하철_노선_찾지_못함(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(노선_2호선_생성_응답.getId(), 노선_수정_요청값());

        // then
        지하철_노선_수정됨(response);
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
        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 10);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(노선_2호선_생성_응답.getId());

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("생성되지 않은 지하철 노선을 제거한다.")
    @Test
    void deleteNotCreatedLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(UNKNOWN_LINE_ID);

        // then
        지하철_노선_찾지_못함(response);
    }

    /**
     * [AS-IS] (강남역)---7m----(선릉역)
     * [ ADD ] (강남역)--4m--(역삼역)
     * [TO-BE] (강남역)--4m--(역삼역)-3m--(선릉역)
     */
    @DisplayName("지하철 구간을 등록한다. (역 사이에 새로운 역을 등록할 경우)")
    @Test
    void addSectionBetweenStations() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 선릉역_생성_응답.getId(), 7);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 4);

        // when
        ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

        // then
        지하철_구간_등록됨(지하철_구간_등록_응답);
        지하철_노선에_지하철_역이_순서대로_등록됨(노선_2호선_생성_응답.getId(), Arrays.asList(
            강남역_생성_응답.getId(),
            역삼역_생성_응답.getId(),
            선릉역_생성_응답.getId()));
    }

    /**
     * [AS-IS]             (역삼역)---7m----(선릉역)
     * [ ADD ] (강남역)--4m--(역삼역)
     * [TO-BE] (강남역)--4m--(역삼역)---7m----(선릉역)
     */
    @DisplayName("지하철 구간을 등록한다. (새로운 역을 상행 종점으로 등록할 경우)")
    @Test
    void addSectionToUpStation() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 7);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 4);

        // when
        ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

        // then
        지하철_구간_등록됨(지하철_구간_등록_응답);
        지하철_노선에_지하철_역이_순서대로_등록됨(노선_2호선_생성_응답.getId(), Arrays.asList(
            강남역_생성_응답.getId(),
            역삼역_생성_응답.getId(),
            선릉역_생성_응답.getId()));
    }

    /**
     * [AS-IS] (강남역)---7m----(역삼역)
     * [ ADD ]                (역삼역)-3m--(선릉역)
     * [TO-BE] (강남역)---7m----(역삼역)-3m--(선릉역)
     *
     */
    @DisplayName("지하철 구간을 등록한다. (새로운 역을 하행 종점으로 등록할 경우)")
    @Test
    void addSectionToDownStation() {
        // given
        StationResponse 강남역_생성_응답 = 지하철_역_등록되어_있음(강남역_생성_요청값());
        StationResponse 역삼역_생성_응답 = 지하철_역_등록되어_있음(역삼역_생성_요청값());
        StationResponse 선릉역_생성_응답 = 지하철_역_등록되어_있음(선릉역_생성_요청값());

        LineCreateRequest 노선_2호선_생성_요청값 = 노선_2호선_생성_요청값(강남역_생성_응답.getId(), 역삼역_생성_응답.getId(), 7);
        LineResponse 노선_2호선_생성_응답 = 지하철_노선_등록되어_있음(노선_2호선_생성_요청값);

        SectionAddRequest 구간_등록_요청값 = SectionFixture.구간_등록_요청값(역삼역_생성_응답.getId(), 선릉역_생성_응답.getId(), 3);

        // when
        ExtractableResponse<Response> 지하철_구간_등록_응답 = 지하철_구간_등록_요청(노선_2호선_생성_응답.getId(), 구간_등록_요청값);

        // then
        지하철_구간_등록됨(지하철_구간_등록_응답);
        지하철_노선에_지하철_역이_순서대로_등록됨(노선_2호선_생성_응답.getId(), Arrays.asList(
            강남역_생성_응답.getId(),
            역삼역_생성_응답.getId(),
            선릉역_생성_응답.getId()));
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineCreateRequest request) {
        return RestAssured.given().log().all()
            .body(request)
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

    private void 지하철_노선_응답_내_역_목록에_상행역_하행역_순으로_조회됨(
        LineResponse lineCreateResponse,
        StationResponse upStationCreateResponse,
        StationResponse downStationCreateResponse
    ) {
        List<Long> actualIds = lineCreateResponse.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        List<Long> expectIds = Stream.of(upStationCreateResponse, downStationCreateResponse)
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualIds).isEqualTo(expectIds);
    }

    private LineResponse 지하철_노선_등록되어_있음(LineCreateRequest request) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private void 잘못된_요청으로_지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 대상을_찾지_못해_지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
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

    private void 지하철_노선_찾지_못함(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineUpdateRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{lineId}", id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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

    private ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, SectionAddRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();
    }

    private void 지하철_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선에_지하철_역이_순서대로_등록됨(Long lineId, List<Long> expectedStationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        LineResponse lineResponse = response.as(LineResponse.class);
        List<Long> actualStationIds = lineResponse.getStations()
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(actualStationIds).isEqualTo(expectedStationIds);
    }
}
