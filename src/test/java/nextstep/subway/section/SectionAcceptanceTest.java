package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTest.지하철_구간_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private static StationResponse 강남역;
    private static StationResponse 양재역;
    private static StationResponse 양재시민의숲;
    private static StationResponse 청계산입구;
    private static StationResponse 광교역;
    private static final int DISTANCE_END_TO_END = 10;

    private static ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(LineResponse lineResponse,
        StationResponse upStation, StationResponse downStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(),
            distance);
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + lineResponse.getId() + "/sections")
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> 지하철_노선에서_구간_제거(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
            .param("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/" + lineId + "/sections")
            .then().log().all()
            .extract();
    }

    private static void 지하철_노선_역_목록_검증(Long lineId, StationResponse... expected) {
        LineResponse line = LineAcceptanceTest.지하철_노선_조회_요청("/lines/" + lineId).as(LineResponse.class);
        assertThat(line.getStations().stream()
            .map(StationResponse::of)
            .collect(Collectors.toList())).containsExactly(expected);
    }

    private static void 지하철_노선_역_거리_검증(Long lineId, Integer... expected) {
        List<SectionResponse> sections = 지하철_구간_조회_요청("/lines/" + lineId + "/sections")
            .jsonPath()
            .getList(".", SectionResponse.class);
        assertThat(sections.stream()
            .map(SectionResponse::getDistance)
            .collect(Collectors.toList())).containsExactly(expected);
    }

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        양재시민의숲 = StationAcceptanceTest.지하철역_등록되어_있음("양재시민의숲").as(StationResponse.class);
        청계산입구 = StationAcceptanceTest.지하철역_등록되어_있음("청계산입구").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음("신분당선", "bg-red-600",
            양재역.getId(), 청계산입구.getId(), DISTANCE_END_TO_END).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다 - 역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection_stationBetweenStations() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 양재역, 양재시민의숲, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        지하철_노선_역_목록_검증(신분당선.getId(), 양재역, 양재시민의숲, 청계산입구);
        지하철_노선_역_거리_검증(신분당선.getId(), 5, 5);
    }

    @DisplayName("노선에 구간을 등록한다 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_newUpStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 양재역, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        지하철_노선_역_목록_검증(신분당선.getId(), 강남역, 양재역, 청계산입구);
        지하철_노선_역_거리_검증(신분당선.getId(), 5, 10);
    }

    @DisplayName("노선에 구간을 등록한다 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_newDownStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 청계산입구, 광교역, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        지하철_노선_역_목록_검증(신분당선.getId(), 양재역, 청계산입구, 광교역);
        지하철_노선_역_거리_검증(신분당선.getId(), 10, 5);
    }

    @DisplayName("구간 등록 시 예외 케이스 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void failAddSection_longerDistance() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 양재역, 양재시민의숲, 13);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 등록 시 예외 케이스 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void failAddSection_stationsExist() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 양재역, 청계산입구, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 등록 시 예외 케이스 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void failAddSection_noStationMatched() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 양재시민의숲, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에서 구간을 제거한다 - 종점 제거")
    @Test
    void removeSection_lastSection() {
        // given
        // line: 양재역-10-청계산입구-5-광교역
        지하철_노선에_구간_등록_요청(신분당선, 청계산입구, 광교역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에서_구간_제거(신분당선.getId(), 광교역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        지하철_노선_역_목록_검증(신분당선.getId(), 양재역, 청계산입구);
        지하철_노선_역_거리_검증(신분당선.getId(), 10);
    }

    @DisplayName("노선에서 구간을 제거한다 - 중간역 제거")
    @Test
    void removeSection_middleSection() {
        // given
        // line: 양재역-10-청계산입구-5-광교역
        지하철_노선에_구간_등록_요청(신분당선, 청계산입구, 광교역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에서_구간_제거(신분당선.getId(), 청계산입구.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        지하철_노선_역_목록_검증(신분당선.getId(), 양재역, 광교역);
        지하철_노선_역_거리_검증(신분당선.getId(), 15);
    }

    @DisplayName("구간 제거 시 예외 케이스 - 구간이 하나인 노선에서 마지막 구간 제거 불가")
    @Test
    void failRemoveSection_lastSectionLeft() {
        // given
        // line: 양재역-10-청계산입구

        // when
        ExtractableResponse<Response> response = 지하철_노선에서_구간_제거(신분당선.getId(), 청계산입구.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        지하철_노선_역_목록_검증(신분당선.getId(), 양재역, 청계산입구);
        지하철_노선_역_거리_검증(신분당선.getId(), 10);
    }

    @DisplayName("구간 제거 시 예외 케이스 - 노선 내에 정류장이 존재하지 않을 때")
    @Test
    void failRemoveSection_stationNotInLine() {
        // given
        // line: 양재역-10-청계산입구-5-광교역
        지하철_노선에_구간_등록_요청(신분당선, 청계산입구, 광교역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에서_구간_제거(신분당선.getId(), 강남역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        지하철_노선_역_목록_검증(신분당선.getId(), 양재역, 청계산입구, 광교역);
        지하철_노선_역_거리_검증(신분당선.getId(), 10, 5);
    }
}
