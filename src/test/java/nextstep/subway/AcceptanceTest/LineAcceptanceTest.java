package nextstep.subway.AcceptanceTest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.LineApi;
import nextstep.subway.api.StationApi;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("classpath:/test-truncate.sql")
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    private final LineApi lineApi = new LineApi();
    private final StationApi stationApi = new StationApi();
    static final String KEY_NAME = "name";

    @LocalServerPort
    int port;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;


    Long upStationId1, upStationId2, downStationId1, downStationId2;
    String lineName1, lineName2, lineColor1, lineColor2;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        upStationId1 = stationApi.createId("광교역");
        downStationId1 = stationApi.createId("신사역");
        upStationId2 = stationApi.createId("석남역");
        downStationId2 = stationApi.createId("장암역");
        lineName1 = "신분당선";
        lineName2 = "분당선";
        lineColor1 = "bg-red-600";
        lineColor2 = "bg-green-600";
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
                = lineApi.create(lineName1, lineColor1, upStationId1, downStationId1);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(lineApi.findNames()).containsAnyOf(lineName1);
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
        lineApi.create(lineName1, lineColor1, upStationId1, downStationId1);
        lineApi.create(lineName2, lineColor2, upStationId2, downStationId2);

        // when
        List<String> lineNames = lineApi.findNames();

        // then
        assertThat(lineNames).containsAnyOf(lineName1, lineName2);
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
        long lineId = lineApi.createId(lineName1, lineColor1, upStationId1, downStationId1);

        // when
        String lineName = lineApi.findName(lineId);

        // then
        assertThat(lineName).isEqualTo(lineName1);
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
        long lineId = lineApi.createId(lineName1, lineColor1, upStationId1, downStationId1);

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
        long lineId = lineApi.createId(lineName1, lineColor1, upStationId1, downStationId1);

        // when
        assertThat(lineApi.delete(lineId).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        assertThat(lineApi.findNames()).doesNotContain(lineName1);

    }
}
