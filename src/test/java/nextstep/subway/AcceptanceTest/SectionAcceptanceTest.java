package nextstep.subway.AcceptanceTest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.api.LineApi;
import nextstep.subway.api.StationApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionAcceptanceTest {
    private final LineApi lineApi = new LineApi();
    private final StationApi stationApi = new StationApi();

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    Long 광교역, 강남역;
    Long 신분당선;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        광교역 = stationApi.createId("광교역");
        강남역 = stationApi.createId("강남역");
        신분당선 = lineApi.create("신분당선", "bg-red-600", 강남역, 광교역).jsonPath().getLong("id");
    }

    @AfterEach
    void cleanUp() {
        databaseCleaner.execute();
    }

    /**
     * When 지하철 노선에 새로운 지하철역 등록을 요청한다.
     * Then 지하철 노선에 지하철역이 등록된다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void create() {
        // when
        Long 양재역 = stationApi.createId("양재역");
        ExtractableResponse<Response> response = lineApi.addSection(신분당선, 강남역, 양재역, 5);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(lineApi.findById(신분당선).jsonPath().getList("stations.name")).contains("양재역")
        );
    }

    /**
     * Given 하행역을 상행 종점역과 동일하게 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선에 지하철역이 등록된다.
     * Then 지하철 노선의 상행 종점역이 새로운 역으로 변경된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createNewEndUpStation() {
        // given
        Long 신논현역 = stationApi.createId("신논현역");

        // when
        ExtractableResponse<Response> response = lineApi.addSection(신분당선, 신논현역, 강남역, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(getLineStationNames(신분당선).get(0)).isEqualTo("신논현역");
    }

    /**
     * Given 상행역을 하행 종점역과 동일하게 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선에 지하철역이 등록된다.
     * Then 지하철 노선의 하행 종점역이 새로운 역으로 변경된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createNewEndDownStation() {
        // given
        Long 호매실역 = stationApi.createId("호매실역");

        // when
        ExtractableResponse<Response> response = lineApi.addSection(신분당선, 광교역, 호매실역, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> lineStationNames = getLineStationNames(신분당선);
        assertThat(lineStationNames.get(lineStationNames.size() - 1)).isEqualTo("호매실역");
    }

    /**
     * Given 지하철 노선에 역을 등록할 경우 기존 역 사이 길이와 크거나 같게 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선등록이 실패한다.
     */
    @DisplayName("새로운 역을 등록할 때 기존 역사이 길이와 크거나 같을 경우 실패한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void createInvalidDistance(Integer distance) {
        // given, when
        ExtractableResponse<Response> response
                = lineApi.addSection(신분당선, 강남역, stationApi.createId("양재역"), distance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 역을 등록할 경우 이미 등록 된 구간을 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선등록이 실패한다.
     * Then 지하철 노선등록 실패에러 메세지를 반환한다.
     */
    @DisplayName("새로운 역을 등록할 때 이미 등록된 구간일 경우 실패한다.")
    @Test
    void createSameRequest() {
        // given, when
        ExtractableResponse<Response> response
                = lineApi.addSection(신분당선, 강남역, 광교역, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("이미 추가된 역입니다.");

    }

    /**
     * Given 지하철 노선에 역을 등록할 경우 노선에 없는 상하행역을 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선등록이 실패한다.
     * Then 지하철 노선등록 실패에러 메세지를 반환한다.
     */
    @DisplayName("기존에 등록된 상하행 역이 없을 경우 실패한다.")
    @Test
    void createUnknownRequest() {
        // given

        Long 신사역 = stationApi.createId("신사역");
        Long 양재역 = stationApi.createId("양재역");

        // when
        ExtractableResponse<Response> response
                = lineApi.addSection(신분당선, 신사역, 양재역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("노선에 등록된 역이 없습니다.");
    }

    private List<String> getLineStationNames(Long id) {
        return lineApi.findById(id).jsonPath().getList("stations.name");
    }
}
