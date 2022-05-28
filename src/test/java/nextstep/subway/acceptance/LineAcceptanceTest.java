package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.test.RequestUtils.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.test.DatabaseClean;
import nextstep.subway.test.ExtractUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능 인수테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    public static final String LINE_PATH = "/lines";

    private static final Map<String, Object> 신분당선 = new HashMap<>();
    private static final Map<String, Object> 분당선 = new HashMap<>();
    private static final Map<String, Object> 중앙선 = new HashMap<>();

    static {
        신분당선.put("name", "신분당선");
        신분당선.put("color", "bg-red-6000");
        신분당선.put("distance", 10);

        분당선.put("name", "분당선");
        분당선.put("color", "bg-yellow-300");
        분당선.put("distance", 10);

        중앙선.put("name", "중앙선");
        중앙선.put("color", "bg-blue-100");
        중앙선.put("distance", 20);
    }

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseClean databaseClean;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseClean.truncateAll();
        setStationIds(신분당선,"판교역","정자역");
        setStationIds(분당선,"죽전역","오리역");
        setStationIds(중앙선,"청량리역","망우역");
    }

    private void setStationIds(Map<String,Object> line, String upStationName, String downStationName) {
        Map<String, Object> upStation = new HashMap<>();
        Map<String, Object> downStation = new HashMap<>();
        upStation.put("name", upStationName);
        downStation.put("name", downStationName);
        line.put("upStationId", ExtractUtils.extractId(requestCreate(upStation, StationAcceptanceTest.STATION_PATH)));
        line.put("downStationId", ExtractUtils.extractId(requestCreate(downStation, StationAcceptanceTest.STATION_PATH)));
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
        ExtractableResponse<Response> createResponse = requestCreate(신분당선, LINE_PATH);

        //then
        노선_생성_및_지하철역들이_노선에_연결되었는지_검증(createResponse);

        //when
        ExtractableResponse<Response> getResponse = requestGetAll(LINE_PATH);

        //then
        목록_조회에서_생성한_노선이_있는지_검증(getResponse);
    }


    private void 노선_생성_및_지하철역들이_노선에_연결되었는지_검증(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(ExtractUtils.extract("stations.name", createResponse, String.class)).contains("판교역", "정자역");
    }

    private void 목록_조회에서_생성한_노선이_있는지_검증(ExtractableResponse<Response> getResponse) {
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ExtractUtils.extractNames(getResponse)).contains("신분당선");
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
        requestCreateBundle(Arrays.asList(신분당선, 분당선), LINE_PATH);

        //when
        ExtractableResponse<Response> response = requestGetAll(LINE_PATH);

        //then
        노선_목록_조회_검증(response);

    }

    private void 노선_목록_조회_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ExtractUtils.extractNames(response)).contains("신분당선", "분당선");
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
        ExtractableResponse<Response> createResponse = requestCreate(신분당선, LINE_PATH);

        //when
        ExtractableResponse<Response> response = requestGetById(LINE_PATH, ExtractUtils.extractId(createResponse));

        //then
        노선_개별_조회_검증(response);
    }

    private void 노선_개별_조회_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(ExtractUtils.extractName(response)).contains("신분당선");
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
        ExtractableResponse<Response> createResponse = requestCreate(중앙선, LINE_PATH);

        //when
        ExtractableResponse<Response> response = requestDeleteById(LINE_PATH, ExtractUtils.extractId(createResponse));

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
        ExtractableResponse<Response> createResponse = requestCreate(신분당선, LINE_PATH);

        //when
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("name", "2호선");
        updateParams.put("color", "bg-green-600");
        ExtractableResponse<Response> response =
                requestUpdateById(LINE_PATH, ExtractUtils.extractId(createResponse), updateParams);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        업데이트한_노선_정보가_변경되었는지_검증(createResponse, updateParams);
    }

    private void 업데이트한_노선_정보가_변경되었는지_검증(ExtractableResponse<Response> createResponse, Map<String, Object> expect) {

        ExtractableResponse<Response> getResponse = requestGetById(LINE_PATH, ExtractUtils.extractId(createResponse));
        assertThat(ExtractUtils.extractName(getResponse)).isEqualTo(expect.get("name"));
        assertThat((String) ExtractUtils.extract("color", getResponse)).isEqualTo(expect.get("color"));
    }
}
