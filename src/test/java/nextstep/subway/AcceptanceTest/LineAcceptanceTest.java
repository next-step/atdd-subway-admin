package nextstep.subway.AcceptanceTest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.api.LineApi;
import nextstep.subway.api.StationApi;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    private final LineApi lineApi = new LineApi();
    private final StationApi stationApi = new StationApi();
    static final String KEY_NAME = "name";

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    Long 광교역, 석남역, 신사역, 장암역;
    String 신분당선, 분당선, 신분당선색상, 분당선색상;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        광교역 = stationApi.createId("광교역");
        신사역 = stationApi.createId("신사역");
        석남역 = stationApi.createId("석남역");
        장암역 = stationApi.createId("장암역");
        신분당선 = "신분당선";
        분당선 = "분당선";
        신분당선색상 = "bg-red-600";
        분당선색상 = "bg-green-600";
    }

    @AfterEach
    void cleanUp() {
        databaseCleaner.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response
                = lineApi.create(신분당선, 신분당선색상, 광교역, 신사역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(lineApi.findNames()).containsAnyOf(신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        lineApi.create(신분당선, 신분당선색상, 광교역, 신사역);
        lineApi.create(분당선, 분당선색상, 석남역, 장암역);

        // when
        List<String> lineNames = lineApi.findNames();

        // then
        assertThat(lineNames).containsAnyOf(신분당선, 분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long lineId = lineApi.createId(신분당선, 신분당선색상, 광교역, 신사역);

        // when
        String lineName = lineApi.findName(lineId);

        // then
        assertThat(lineName).isEqualTo(신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long lineId = lineApi.createId(신분당선, 신분당선색상, 광교역, 신사역);

        // when
        String updateLineName = "뉴분당선";
        ExtractableResponse<Response> response = lineApi.update(lineId, updateLineName, "bg-red-600");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        assertThat(lineApi.findById(lineId).jsonPath().getString(KEY_NAME)).isEqualTo(updateLineName);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        long lineId = lineApi.createId(신분당선, 신분당선색상, 광교역, 신사역);

        // when
        ExtractableResponse<Response> response = lineApi.delete(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
