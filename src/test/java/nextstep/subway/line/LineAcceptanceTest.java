package nextstep.subway.line;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse 대화역;
    private StationResponse 양재역;
    private StationResponse 수서역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        대화역 = StationAcceptanceTest.createStation("대화");
        양재역 = StationAcceptanceTest.createStation("양재");
        수서역 = StationAcceptanceTest.createStation("수서");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertAll(
            () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> Assertions.assertThat(response.as(LineResponse.class)).isEqualTo(LineResponse.of(new Line(lineRequest.getName(), lineRequest.getColor())))
        );
    }

    @DisplayName("구간을 포함하여 지하철 노선을 생성한다.")
    @Test
    void createLineWithSection() {
        // given
        StationResponse upStation = 대화역;
        StationResponse downStation =  수서역;

        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);

        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertAll(
            () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> Assertions.assertThat(response.as(LineResponse.class)).isEqualTo(LineResponse.of(new Line(lineRequest.getName(), lineRequest.getColor())))
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createSubwayLine(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("신분당선", "bg-blue-600");

        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간 정보가 포함되지 않은 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        createSubwayLine(new LineRequest("신분당선", "bg-red-600"));
        createSubwayLine(new LineRequest("2호선", "bg-green-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestSearchLineInfo(null);

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertAll(
            () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)).hasSize(2),
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[0]).isEqualTo(LineInfoResponse.of(new Line("신분당선", "bg-red-600"))),
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[1]).isEqualTo(LineInfoResponse.of(new Line("2호선", "bg-green-600")))
        );
    }

    @DisplayName("구간 정보가 미포함된 지하철 노선을 조회한다.")
    @Test
    void getLineWithoutSection() {
        // given
        // 지하철_노선_등록되어_있음
        createSubwayLine(new LineRequest("신분당선", "bg-red-600"));
        createSubwayLine(new LineRequest("2호선", "bg-green-600"));

        LineInfoResponse searchingLine = Arrays.stream(requestSearchLineInfo(null).as(LineInfoResponse[].class))
                                                .filter(item -> item.getName().equals("2호선"))
                                                .findFirst()
                                                .get();
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = requestSearchLineInfo(searchingLine.getId());

        // then
        // 지하철_노선_응답됨
        assertAll(
            () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> Assertions.assertThat(response.as(LineInfoResponse.class)).isEqualTo(LineInfoResponse.of(new Line(searchingLine.getName(), searchingLine.getColor())))
        );
    }

    @DisplayName("구간 정보가 포함된 지하철 노선을 조회한다.")
    @Test
    void getLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse upStation = 대화역;
        StationResponse downStation = 수서역;

        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10));
        createSubwayLine(new LineRequest("2호선", "bg-green-600"));

        LineInfoResponse searchingLine = Arrays.stream(requestSearchLineInfo(null).as(LineInfoResponse[].class))
                                                .filter(item -> item.getName().equals("신분당선"))
                                                .findFirst()
                                                .get();

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestSearchLineInfo(searchingLine.getId());

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        List<StationResponse> expectedStations = List.of(upStation, downStation);

        LineInfoResponse expectedlineInfoResponse = LineInfoResponse.of(new Line(redLine.getName(), redLine.getColor()), expectedStations);

        assertAll(
            () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> Assertions.assertThat(response.as(LineInfoResponse.class)).isEqualTo(expectedlineInfoResponse)
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_수정_요청
        LineRequest lineRequest = new LineRequest("구분당선", "bg-blue-600");
        ExtractableResponse<Response> response = requestUpdateLine(redLine.getId(), lineRequest);

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> searchResponse = requestSearchLineInfo(null);

        assertAll(
            () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> Assertions.assertThat(searchResponse.as(LineInfoResponse[].class)[0]).isEqualTo(LineInfoResponse.of(new Line(lineRequest.getName(), lineRequest.getColor())))
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = requestDeleteLine(redLine.getId());

        // then
        // 지하철_노선_삭제됨
        ExtractableResponse<Response> searchResponse = requestSearchLineInfo(null);

        assertAll(
            () -> Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> Assertions.assertThat(searchResponse.as(LineInfoResponse[].class)).isEmpty()
        );
    }

    @DisplayName("기등록된 구간이 있는 노선에 신규 구간을 추가한다.")
    @Test
    void addSection_hasSectionLine() {
        // given
        StationResponse upStation = 대화역;
        StationResponse downStation = 수서역;
        StationResponse newStation = 양재역;

        LineResponse orangeLine = createSubwayLine(new LineRequest("3호선", "bg-orange-600", upStation.getId(), downStation.getId(), 300));

        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), newStation.getId(), 130);

        // when
        ExtractableResponse<Response> response = requestAddSection(orangeLine.getId(), sectionRequest);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> serachResponse = requestSearchLineInfo(orangeLine.getId());
        Assertions.assertThat(serachResponse.as(LineInfoResponse.class).getStations()).hasSize(3);
    }

    @DisplayName("2개의 구간이 등록된 경우 상행종점역을 제거한다.")
    @Test
    void deleteSection_hasMultiSection_deleteUpStation() {
        // given
        LineResponse 삼호선 = createSubwayLine(new LineRequest("삼호선", "bg-orange-600", 대화역.getId(), 수서역.getId(), 300));

        createMultiSectionLine(삼호선, 대화역, 양재역, 130);

        Long targetLineId = 삼호선.getId();
        Long deletingStationId = 대화역.getId();

        List<StationResponse> expectedStationResponse = List.of(양재역, 수서역);

        // when
        ExtractableResponse<Response> deleteResposne = requestDeleteSection(targetLineId, deletingStationId);

        // then
        ExtractableResponse<Response> findResposne = requestSearchLineInfo(targetLineId);

        assertAll(
            () -> Assertions.assertThat(deleteResposne.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> Assertions.assertThat(findResposne.as(LineInfoResponse.class).getStations()).isEqualTo(expectedStationResponse)
        );
    }

    @DisplayName("2개의 구간이 등록된 경우 하행종점역을 제거한다.")
    @Test
    void deleteSection_hasMultiSection_deleteDownStation() {
        // given
        LineResponse 삼호선 = createSubwayLine(new LineRequest("삼호선", "bg-orange-600", 대화역.getId(), 수서역.getId(), 300));

        createMultiSectionLine(삼호선, 대화역, 양재역, 130);

        List<LineInfoResponse> stationResponses = List.of(requestSearchAllLineInfo().as(LineInfoResponse[].class));

        Long targetLineId = 삼호선.getId();
        Long deletingStationId = 수서역.getId();

        // when
        ExtractableResponse<Response> deleteResposne = requestDeleteSection(targetLineId, deletingStationId);

        // then
        ExtractableResponse<Response> findResposne = requestSearchLineInfo(targetLineId);
        assertAll(
            () -> Assertions.assertThat(deleteResposne.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> Assertions.assertThat(findResposne.as(LineInfoResponse.class).getStations()).isEqualTo(List.of(StationResponse.of(new Station("대화")), StationResponse.of(new Station("양재"))))
        );
    }

    @DisplayName("2개의 구간이 등록된 경우 중간역을 제거한다.")
    @Test
    void deleteSection_hasMultiSection_deleteMidStation() {
        // given
        LineResponse 삼호선 = createSubwayLine(new LineRequest("삼호선", "bg-orange-600", 대화역.getId(), 수서역.getId(), 300));

        createMultiSectionLine(삼호선, 대화역, 양재역, 130);

        Long targetLineId = 삼호선.getId();
        Long deletingStationId = 양재역.getId();

        // when
        ExtractableResponse<Response> deleteResposne = requestDeleteSection(targetLineId, deletingStationId);

        // then
        ExtractableResponse<Response> findResposne = requestSearchLineInfo(targetLineId);
        assertAll(
            () -> Assertions.assertThat(deleteResposne.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> Assertions.assertThat(findResposne.as(LineInfoResponse.class).getStations()).isEqualTo(List.of(StationResponse.of(new Station("대화")), StationResponse.of(new Station("수서"))))
        );
    }

    @DisplayName("1개의 구간이 등록된 경우 상행 종점역을 제거시 에러를 발생한다.")
    @Test
    void deleteSection_hasOneSection_deleteUpStation() {
        // given
        LineResponse 삼호선 = createSubwayLine(new LineRequest("삼호선", "bg-orange-600", 대화역.getId(), 수서역.getId(), 300));

        // when
        // then
        Assertions.assertThat(requestDeleteSection(삼호선.getId(), 대화역.getId()).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("1개의 구간이 등록된 경우 하행 종점역을 제거시 에러를 발생한다.")
    @Test
    void deleteSection_hasOneSection_deleteDownStation() {
        // given
        LineResponse 삼호선 = createSubwayLine(new LineRequest("삼호선", "bg-orange-600", 대화역.getId(), 수서역.getId(), 300));

        // when
        // then
        Assertions.assertThat(requestDeleteSection(삼호선.getId(), 수서역.getId()).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void createSingleSectionLine(StationResponse upStation, StationResponse downStation, int distance) {
        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), distance));
    }

    private void createMultiSectionLine(LineResponse line, StationResponse newUpStation, StationResponse newDownStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(newUpStation.getId(), newDownStation.getId(), distance);

        // when
        ExtractableResponse<Response> response = requestAddSection(line.getId(), sectionRequest);
    }
    private LineResponse createSubwayLine(LineRequest lineRequest) {
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        return response.as(LineResponse.class);
    }

    private ExtractableResponse<Response> requestAddSection(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all().
                            body(sectionRequest).
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            post("/lines/" + String.valueOf(lineId) + "/sections").
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestCreateLine(LineRequest lineRequest) {
        return RestAssured.given().log().all().
                            body(lineRequest).
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            post("/lines").
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestSearchLineInfo(Long subwayLineId) {
        String requestLineIdURL = "";

        if (subwayLineId != null) {
            requestLineIdURL = "/" + subwayLineId;
        }

        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            get("/lines" + requestLineIdURL).
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestSearchAllLineInfo() {
        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            get("/lines").
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestUpdateLine(Long subwayLineId, LineRequest lineRequest) {
        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            body(lineRequest).
                            when().
                            put("/lines/" + subwayLineId).
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestDeleteLine(Long subwayLineId) {
        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            delete("/lines/" + subwayLineId).
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestDeleteSection(Long subwayLineId, Long StationId) {
        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            delete("/lines/" + subwayLineId + "/sections?stationId=" + StationId).
                            then().
                            log().all().
                            extract();
    }
}
