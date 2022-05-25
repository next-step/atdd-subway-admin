package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.test.RequestUtils.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@Sql(scripts = "/drop_test_table.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    private static final String LINE_PATH = "/lines";
    private static final int SHIN_BUN_DANG_LINE = 0;
    private static final List<Map<String, Object>> LINE_PARAMS_BUNDLES;

    static {
        Map<String, Object> params1 = new HashMap<>();
        Map<String, Object> params2 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-6000");
        params1.put("upStationId", 1);
        params1.put("downStationId", 2);
        params1.put("distance", 10);

        params2.put("name", "2호선");
        params2.put("color", "bg-green-6000");
        params2.put("upStationId", 1);
        params2.put("downStationId", 2);
        params2.put("distance", 10);
        LINE_PARAMS_BUNDLES = Arrays.asList(params1, params2);
    }

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        // 지하철역 2개 생성
        requestCreateBundle(StationAcceptanceTest.STATION_PATH, StationAcceptanceTest.STATION_PARAMS_BUNDLES);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 생성된 지하철 노선 정보를 응답받는다.
     * When 지하철 노선 목록을 조회하면
     * Then 생성한 노선을 찾을 수 있다.
     * */
    @DisplayName("지하철 노선을 생성하여 조회 시 있는지 확인한다.")
    @Test
    void createLines() {

        //when
        ExtractableResponse<Response> createResponse = requestCreate(LINE_PATH,
                LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //then
        노선_생성_및_지하철역들이_노선에_연결되었는지_검증(createResponse);

        //when
        ExtractableResponse<Response> getResponse = requestGetAll(LINE_PATH);

        //then
        목록_조회에서_생성한_노선이_있는지_검증(getResponse);
    }


    private void 노선_생성_및_지하철역들이_노선에_연결되었는지_검증(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.jsonPath().getList("stations.name")).contains("서울역", "강남역");
    }

    private void 목록_조회에서_생성한_노선이_있는지_검증(ExtractableResponse<Response> getResponse) {
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("name")).contains("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회시 2개의 노선을 조회할 수 있다.
     * */
    @DisplayName("생성된 지하철 노선들이 목록 조회 시 있는지 확인한다.")
    @Test
    void showLines() {

        //given
        requestCreateBundle(LINE_PATH, LINE_PARAMS_BUNDLES);

        //when
        ExtractableResponse<Response> response = requestGetAll(LINE_PATH);

        //then
        노선_목록_조회_검증(response);

    }

    private void 노선_목록_조회_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).contains("신분당선", "2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * */
    @DisplayName("지하철 노선을 개별 조회한다.")
    @Test
    void showLine() {
        //given
        requestCreate(LINE_PATH, LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //when
        ExtractableResponse<Response> response = requestGetById(LINE_PATH, 1L);

        //then
        노선_개별_조회_검증(response);
    }

    private void 노선_개별_조회_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).contains("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        requestCreate(LINE_PATH, LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //when
        ExtractableResponse<Response> response = requestDeleteById(LINE_PATH, 1L);

        //then
        삭제된_노선_조회시_오류_응답_검증(response);
    }

    private void 삭제된_노선_조회시_오류_응답_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(requestGetById(LINE_PATH, 1L).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * */
    @DisplayName("지하철 노선을 업데이트한다.")
    @Test
    void updateLine() {
        //given
        requestCreate(LINE_PATH, LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE));

        //when
        Map<String, Object> lineParams = LINE_PARAMS_BUNDLES.get(SHIN_BUN_DANG_LINE);
        lineParams.put("name", "분당선");
        lineParams.put("color", "bg-yellow-600");
        ExtractableResponse<Response> response = requestUpdateById(LINE_PATH, 1L, lineParams);

        //then
        업데이트한_노선_정보가_변경되었는지_검증(response);
    }

    private void 업데이트한_노선_정보가_변경되었는지_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> getResponse = requestGetById(LINE_PATH, 1L);
        assertThat(getResponse.jsonPath().getString("name")).contains("분당선");
        assertThat(getResponse.jsonPath().getString("color")).contains("bg-yellow-600");
    }
}
