package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.AddSectionRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철구간 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionAcceptanceTest {
    private static long 초기구간길이 = 10;

    @LocalServerPort
    int port;
    StationResponse 상행선;
    StationResponse 하행선;
    String 노선;

    /**
     * Given 구간(상행선-하행선)을 갖는 노선을 생성한다
     */
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        // given
        상행선 = 지하철역_생성("상행선");
        하행선 = 지하철역_생성("하행선");
        노선 = 노선_생성("2호선", "green", 상행선, 하행선, 초기구간길이);
    }

    /**
     * Given 신규역을 생성하고
     * When 구간(상행선-신규역)을 추가하면
     * Then 노선 조회 시 상행선-신규역-하행선 순서로 조회된다
     */
    @DisplayName("구간(상행선-신규역)을 추가한다.")
    @Test
    void createInsideLeft() {
        // given
        StationResponse 신규역 = 지하철역_생성("신규역");

        // when
        구간추가_성공(노선, 상행선, 신규역, 3);

        // then
        노선_순서대로_조회_성공(노선, 상행선, 신규역, 하행선);
    }

    /**
     * Given 신규역을 생성하고
     * When 구간(신규역-하행선)을 추가하면
     * Then 노선 조회 시 상행선-신규역-하행선 순서로 조회된다
     */
    @DisplayName("구간(신규역-하행선)을 추가한다.")
    @Test
    void createInsideRight() {
        // given
        StationResponse 신규역 = 지하철역_생성("신규역");

        // when
        구간추가_성공(노선, 신규역, 하행선, 3);

        // then
        노선_순서대로_조회_성공(노선, 상행선, 신규역, 하행선);
    }

    /**
     * Given 신규역을 생성하고
     * When 구간(신규역-상행선)을 추가하면
     * Then 신규역이 상행종점역으로 조회된다
     */
    @DisplayName("상행종점역을 추가한다.")
    @Test
    void createOutsideLeft() {
        // given
        StationResponse 신규역 = 지하철역_생성("신규역");

        // when
        구간추가_성공(노선, 신규역, 상행선, 3);

        // then
        노선_상행종점역_조회_성공(노선, 신규역);
    }

    /**
     * Given 신규역을 생성하고
     * When 구간(하행선-신규역)을 추가하면
     * Then 신규역이 하행종점역으로 조회된다
     */
    @DisplayName("하행종점역을 추가한다.")
    @Test
    void createOutsideRight() {
        // given
        StationResponse 신규역 = 지하철역_생성("신규역");

        // when
        구간추가_성공(노선, 하행선, 신규역, 3);

        // then
        노선_하행종점역_조회_성공(노선, 신규역);
    }

    /**
     * Given 신규역을 생성하고
     * When 구간(상행선-신규역)을 기존역보다 길거나 같게 추가하면
     * Then 실패한다
     */
    @DisplayName("역과 역 사이에 새로운 역 추가 시 기존 역 사이보다 길거나 같으면 등록할 수 없다")
    @Test
    void createTooLong() {
        // given
        StationResponse 신규역 = 지하철역_생성("신규역");

        // when
        // then
        구간추가_실패(노선, 상행선, 신규역, 초기구간길이);
        구간추가_실패(노선, 상행선, 신규역, 초기구간길이 + 1);
    }

    /**
     * Given 구간을 추가하고
     * When 이미 등록되어 있는 구간을 추가하면
     * Then 실패한다
     */
    @DisplayName("등록하는 구간의 상행역과 하행역이 이미 등록되어 있다면 등록할 수 없다")
    @Test
    void createSameSection() {
        // given
        StationResponse 중간역 = 지하철역_생성("중간역");
        구간추가_성공(노선, 상행선, 중간역, 5);

        // when
        // then
        구간추가_실패(노선, 중간역, 상행선, 3);
        구간추가_실패(노선, 중간역, 하행선, 3);
        구간추가_실패(노선, 하행선, 상행선, 3);
    }

    /**
     * Given 새로운 역 두개를 추가하고
     * When 새로운 역으로만 이루어진 구간을 추가하면
     * Then 실패한다
     */
    @DisplayName("등록하는 구간의 상행역과 하행역이 하나도 등록되어있지 않다면 등록할 수 없다")
    @Test
    void createUnknownSection() {
        // given
        StationResponse 신규역1 = 지하철역_생성("신규역1");
        StationResponse 신규역2 = 지하철역_생성("신규역2");

        // when
        // then
        구간추가_실패(노선, 신규역1, 신규역2, 3);
    }

    StationResponse 지하철역_생성(String name) {
        return StationApi.create(name)
                .as(StationResponse.class);
    }

    String 노선_생성(String name, String color, StationResponse upStation, StationResponse downStation, long distance) {
        return LineApi.create(new LineRequest(name, color, upStation.getId(), downStation.getId(), distance))
                .header("location");
    }

    void 구간추가_성공(String lineLocation, StationResponse upStation, StationResponse downStation, long distance) {
        ExtractableResponse<Response> response = LineApi.addSection(lineLocation, new AddSectionRequest(upStation.getId(), downStation.getId(), distance));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    void 구간추가_실패(String lineLocation, StationResponse upStation, StationResponse downStation, long distance) {
        ExtractableResponse<Response> response = LineApi.addSection(lineLocation, new AddSectionRequest(upStation.getId(), downStation.getId(), distance));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    void 노선_순서대로_조회_성공(String lineLocation, StationResponse... stations) {
        List<Long> findStationIds = LineApi.find(lineLocation)
                .jsonPath()
                .getList("stations.id", Long.class);
        List<Long> stationIds = Arrays.stream(stations)
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(findStationIds).containsSequence(stationIds);
    }

    void 노선_상행종점역_조회_성공(String lineLocation, StationResponse station) {
        Long findBeginId = LineApi.find(lineLocation)
                .jsonPath()
                .getLong("stations[0].id");
        assertThat(findBeginId).isEqualTo(station.getId());
    }

    void 노선_하행종점역_조회_성공(String lineLocation, StationResponse station) {
        Long findBeginId = LineApi.find(lineLocation)
                .jsonPath()
                .getLong("stations[-1].id");
        assertThat(findBeginId).isEqualTo(station.getId());
    }
}
