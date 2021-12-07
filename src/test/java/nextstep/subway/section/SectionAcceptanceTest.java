package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.지하철_역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationService stationService;

    private Long lineId;

    private Long upStationId;

    private Long downStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철_역_생성_요청("강남역");
        ExtractableResponse<Response> 광교역_생성_응답 = 지하철_역_생성_요청("광교역");

        upStationId = 역_ID(강남역_생성_응답);
        downStationId = 역_ID(광교역_생성_응답);

        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청("신분당선", "red", upStationId, downStationId, 7);

        lineId = 노선_ID(신분당선_생성_응답);
    }

    @DisplayName("지하철 노선 생성시 구간을 생성한다.")
    @Test
    void createSection() {
        // then
        지하철_구간_생성됨(upStationId, downStationId);
    }

    @DisplayName("노선에 구간 추가 성공 후 노선 조회")
    @Test
    void addSection_success_findLine() {
        // given
        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, upStationId, 역_ID(양재역_생성_응답), 4);
        int lineId = response.jsonPath().getInt("id");

        // then
        assertThat(지하철_노선_조회_요청(lineId).jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "양재역", "광교역");
    }

    @DisplayName("노선에 구간 추가 성공 - 역 사이에 새로운 하행 역을 등록한다.")
    @Test
    void addSection_success_betweenStation1() {
        // given
        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, upStationId, 역_ID(양재역_생성_응답), 4);

        // then
        Assertions.assertAll(
                () -> 지하철_구간_추가됨(response)
                , () -> 지하철_역_정렬됨(response, "강남역", "양재역", "광교역")
                , () -> 지하철_노선_총거리(response, 7));
    }

    @DisplayName("노선에 구간 추가 성공 - 역 사이에 새로운 상행 역을 등록한다.")
    @Test
    void addSection_success_betweenStation2() {
        // given
        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, 역_ID(양재역_생성_응답), downStationId, 3);

        // then
        Assertions.assertAll(
                () -> 지하철_구간_추가됨(response)
                , () -> 지하철_역_정렬됨(response, "강남역", "양재역", "광교역")
                , () -> 지하철_노선_총거리(response, 7));
    }

    @DisplayName("노선에 구간 추가 성공 - 역 사이에 새로운 역을 2개 등록한다.")
    @Test
    void addSection_success_betweenTwice() {
        // given
        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");
        지하철_구간_추가_요청(lineId, upStationId, 역_ID(양재역_생성_응답), 4);

        // when
        ExtractableResponse<Response> 판교역_생성_응답 = 지하철_역_생성_요청("판교역");
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, 역_ID(양재역_생성_응답), 역_ID(판교역_생성_응답), 1);

        // then
        Assertions.assertAll(
                () -> 지하철_구간_추가됨(response)
                , () -> 지하철_역_정렬됨(response, "강남역", "양재역", "판교역", "광교역")
                , () -> 지하철_노선_총거리(response, 7));
    }

    @DisplayName("노선에 구간 추가 성공 - 새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSection_success_upStation() {
        // given
        ExtractableResponse<Response> 용산역_생성_응답 = 지하철_역_생성_요청("용산역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, 역_ID(용산역_생성_응답), upStationId, 4);

        // then
        Assertions.assertAll(
                () -> 지하철_구간_추가됨(response)
                , () -> 지하철_역_정렬됨(response, "용산역", "강남역", "광교역")
                , () -> 지하철_노선_총거리(response, 11));
    }

    @DisplayName("노선에 구간 추가 성공 - 새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSection_success_downStation() {
        // given
        ExtractableResponse<Response> 호매실역_생성_응답 = 지하철_역_생성_요청("호매실역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, downStationId, 역_ID(호매실역_생성_응답), 3);

        // then
        Assertions.assertAll(
                () -> 지하철_구간_추가됨(response)
                , () -> 지하철_역_정렬됨(response, "강남역", "광교역", "호매실역")
                , () -> 지하철_노선_총거리(response, 10));
    }

    @DisplayName("노선에 구간 추가 실패 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @ParameterizedTest(name = "{displayName}{index} -> distance: {0}")
    @ValueSource(ints = {7, 8})
    void addSection_failure_distance(int distance) {
        // given
        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, upStationId, 역_ID(양재역_생성_응답), distance);

        // then
        지하철_구간_추가_실패됨(response);
    }

    @DisplayName("노선에 구간 추가 실패 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void addSection_failure_duplicate() {
        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, upStationId, downStationId, 7);

        // then
        지하철_구간_추가_실패됨(response);
    }

    @DisplayName("노선에 구간 추가 실패 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void addSection_failure_notInclude() {
        // given
        ExtractableResponse<Response> 양재역_생성_응답 = 지하철_역_생성_요청("양재역");
        ExtractableResponse<Response> 판교역_생성_응답 = 지하철_역_생성_요청("판교역");

        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineId, 역_ID(양재역_생성_응답), 역_ID(판교역_생성_응답), 5);

        // then
        지하철_구간_추가_실패됨(response);
    }

    private void 지하철_구간_생성됨(long upStationId, long downStationId) {
        Section section = 지하철_구간_조회(upStationId, downStationId);
        Assertions.assertAll(
                () -> assertThat(section.getUpStation().getId()).isEqualTo(upStationId)
                , () -> assertThat(section.getDownStation().getId()).isEqualTo(downStationId)
        );
    }

    private Section 지하철_구간_조회(long upStationId, long downStationId) {
        Station upStation = stationService.findStation(upStationId);
        Station downStation = stationService.findStation(downStationId);

        return sectionRepository.findByUpStationAndDownStation(upStation, downStation)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("존재하지 않는 구간입니다. (upStationId: %d, downStationId: %d)", upStation.getId(), downStation.getId())));
    }

    public static ExtractableResponse<Response> 지하철_구간_추가_요청(long lineId, long upStationId, long downStationId, int distance) {
        SectionRequest params = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    private void 지하철_구간_추가됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_구간_추가_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    private void 지하철_역_정렬됨(ExtractableResponse<Response> response, String... expect) {
        List<String> names = response.jsonPath().getList("stations.name", String.class);
        assertThat(names).containsExactly(expect);
    }

    private void 지하철_노선_총거리(ExtractableResponse<Response> response, int distance) {
        Long lineId = response.jsonPath().getLong("id");
        Line line = lineRepository.findById(lineId).get();

        Integer totalDistance = line.getSections().stream()
                .map(Section::getDistance)
                .reduce(Integer::sum).get();

        assertThat(totalDistance).isEqualTo(distance);
    }
}
