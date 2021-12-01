package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.exception.NotAcceptableApiException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static nextstep.subway.section.SectionAcceptanceTest.지하철_구간_추가_요청;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationService stationService;

    private Long 강남역_ID;

    private Long 광교역_ID;

    private int distance;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청("강남역");
        ExtractableResponse<Response> 광교역_생성_응답 = 지하철_역_생성_요청("광교역");

        강남역_ID = 역_ID(강남역_생성_응답);
        광교역_ID = 역_ID(광교역_생성_응답);
        distance = 10;
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);

        ExtractableResponse<Response> 역삼역_생성_응답 = 지하철_역_생성_요청("역삼역");
        long 역삼역_ID = 역_ID(역삼역_생성_응답);
        지하철_노선_생성_요청("2호선", "green", 강남역_ID, 역삼역_ID, distance);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);
        long 신분당선_ID = 노선_ID(신분당선_생성_응답);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선_ID);

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);
        long 신분당선_ID = 노선_ID(신분당선_생성_응답);

        // when
        LineRequest lineRequest = new LineRequest("2호선", "green");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(신분당선_ID, lineRequest);

        // then
        지하철_노선_수정됨(response, lineRequest);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선_생성_응답.header("Location"));

        // then
        지하철_노선_제거됨(response);
    }

    @DisplayName("노선의 구간을 제거한다. - 상행 종점을 제거하면 해당 구간의 하행 역이 종점이 된다.")
    @Test
    void deleteSection_ascendingEndPoint() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);
        long 신분당선_ID = 노선_ID(신분당선_생성_응답);

        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");
        long 양재역_ID = 역_ID(양재역_생성_응답);
        지하철_구간_추가_요청(신분당선_ID, 강남역_ID, 양재역_ID, 4);

        // when
        ExtractableResponse<Response> 신분당선_조회_응답 = 지하철_노선_조회_요청(신분당선_ID);
        long ascendingEndPointId = 신분당선_조회_응답.jsonPath().getInt("stations[0].id");
        지하철_노선_구간_제거_요청(신분당선_ID, ascendingEndPointId);

        // then
        ExtractableResponse<Response> 노선_구간_제거후_신분당선_조회_응답 = 지하철_노선_조회_요청(신분당선_ID);
        long newAscendingEndPointId = 노선_구간_제거후_신분당선_조회_응답.jsonPath().getInt("stations[0].id");
        assertThat(newAscendingEndPointId).isEqualTo(양재역_ID);
    }

    @DisplayName("노선의 구간을 제거한다. - 하행 종점을 제거하면 해당 구간의 상행 역이 종점이 된다.")
    @Test
    void deleteSection_descendingEndpoint() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);
        long 신분당선_ID = 노선_ID(신분당선_생성_응답);

        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");
        long 양재역_ID = 역_ID(양재역_생성_응답);
        지하철_구간_추가_요청(신분당선_ID, 강남역_ID, 양재역_ID, 4);

        // when
        ExtractableResponse<Response> 신분당선_조회_응답 = 지하철_노선_조회_요청(신분당선_ID);
        long descendingEndPointId = 신분당선_조회_응답.jsonPath().getInt("stations[2].id");
        지하철_노선_구간_제거_요청(신분당선_ID, descendingEndPointId);

        // then
        ExtractableResponse<Response> 노선_구간_제거후_신분당선_조회_응답 = 지하철_노선_조회_요청(신분당선_ID);
        long newAscendingEndPointId = 노선_구간_제거후_신분당선_조회_응답.jsonPath().getInt("stations[2].id");
        assertThat(newAscendingEndPointId).isEqualTo(양재역_ID);
    }

    @DisplayName("노선의 구간을 제거한다. - 중간역이 제거될 경우 구간을 재배치 한다.")
    @Test
    void deleteSection_betweenStation() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);
        long 신분당선_ID = 노선_ID(신분당선_생성_응답);

        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");
        long 양재역_ID = 역_ID(양재역_생성_응답);
        지하철_구간_추가_요청(신분당선_ID, 강남역_ID, 양재역_ID, 4);

        // when
        지하철_노선_구간_제거_요청(신분당선_ID, 양재역_ID);

        // then
        Section section = 지하철_구간_조회(강남역_ID, 광교역_ID);
        assertThat(section.getDistance()).isEqualTo(distance);
    }

    @DisplayName("노선의 구간을 제거한다. - 노선에 등록되어있지 않은 역은 제거할 수 없다.")
    @Test
    void deleteSection_notExistStation() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);
        long 신분당선_ID = 노선_ID(신분당선_생성_응답);

        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");
        long 양재역_ID = 역_ID(양재역_생성_응답);
        지하철_구간_추가_요청(신분당선_ID, 강남역_ID, 양재역_ID, 4);

        ExtractableResponse<Response> 판교역_생성_응답 = 지하철_역_생성_요청("판교역");
        long 판교역_ID = 역_ID(판교역_생성_응답);

        // when & then
        assertThatExceptionOfType(NotAcceptableApiException.class)
                .isThrownBy(() -> 지하철_노선_구간_제거_요청(신분당선_ID, 판교역_ID));
    }

    @DisplayName("노선의 구간을 제거한다. - 구간이 하나인 노선에서 마지막 구간은 제거할 수 없다.")
    @Test
    void deleteSection_onlyOneSection() {
        // given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", 강남역_ID, 광교역_ID, distance);
        long 신분당선_ID = 노선_ID(신분당선_생성_응답);

        // when & then
        assertThatExceptionOfType(NotAcceptableApiException.class)
                .isThrownBy(() -> 지하철_노선_구간_제거_요청(신분당선_ID, 강남역_ID));
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest params = new LineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .when()
                .get("/lines/{lineId}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(long lineId, LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());

        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{lineId}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String uri) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineRequest lineRequest) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.jsonPath().get("name").toString()).hasToString(lineRequest.getName())
                , () -> assertThat(response.jsonPath().get("color").toString()).hasToString(lineRequest.getColor())
        );
    }

    private void 지하철_노선_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> 지하철_노선_목록_응답됨(response)
                , () -> 지하철_노선_목록_포함됨(response, Arrays.asList("신분당선", "2호선"))
        );
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<String> names) {
        assertThat(response.jsonPath().getList("name", String.class).containsAll(names)).isTrue();
    }

    private void 지하철_노선_구간_제거_요청(long lineId, long ascendingEndPointId) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId", lineId)
                .queryParam("stationId", ascendingEndPointId)
                .when()
                .delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    public static long 노선_ID(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static long 역_ID(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    private Section 지하철_구간_조회(long upStationId, long downStationId) {
        Station upStation = stationService.findStation(upStationId);
        Station downStation = stationService.findStation(downStationId);

        return sectionRepository.findByUpStationAndDownStation(upStation, downStation)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("존재하지 않는 구간입니다. (upStationId: %d, downStationId: %d)", upStation.getId(), downStation.getId())));
    }
}
