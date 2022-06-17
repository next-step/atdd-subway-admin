package nextstep.subway.station;

import static nextstep.subway.utils.AssertionsUtils.assertCreated;
import static nextstep.subway.utils.AssertionsUtils.assertInternalServerError;
import static nextstep.subway.utils.AssertionsUtils.assertNoContent;
import static nextstep.subway.utils.AssertionsUtils.assertOk;
import static nextstep.subway.utils.LineAcceptanceTestUtils.generateLine;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getId;
import static nextstep.subway.utils.RestAssuredUtils.delete;
import static nextstep.subway.utils.RestAssuredUtils.get;
import static nextstep.subway.utils.RestAssuredUtils.put;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nextstep.subway.dto.UpdateLineRequest;
import nextstep.subway.utils.TearDownUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(TearDownUtils.class)
public class LineAcceptanceTest {

    private final TearDownUtils tearDownUtils;

    public LineAcceptanceTest(TearDownUtils tearDownUtils) {
        this.tearDownUtils = tearDownUtils;
    }

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        tearDownUtils.tableClear();
    }

    public static final String LINE_BASE_URL = "/lines";

    /**
     * When 지하철 노선을 생성하면 Then 지하철 목록 정보에서 생성한 지하철 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    public void createLine() {
        // When
        Response createLineResponse = generateLine("신분당선", "강남역", "판교역");
        Response getAllStationsResponse = get(LINE_BASE_URL).extract().response();

        // Then
        JsonPath createLineResponseBody = createLineResponse.jsonPath();
        JsonPath getAllStationsResponseBody = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertCreated(createLineResponse),
            () -> assertThat(createLineResponseBody.getString("name")).isEqualTo("신분당선"),
            () -> assertThat(createLineResponseBody.getList("stations.name"))
                .containsAnyOf("강남역", "판교역"),
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(getAllStationsResponseBody.getList("name"))
                .as("지하철 목록 정보에서 생성한 지하철 노선 포함 여부 검증")
                .containsAnyOf("신분당선")
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 목록 정보를 조회하면 Then 생성한 지하철 노선 정보를 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    public void getAllLines() {
        // Given
        Response createFirstLineResponse = generateLine("신분당선", "강남역", "판교역");
        Response createSecondLineResponse = generateLine("분당선", "문정역", "야탑역");

        // When
        Response getAllStationsResponse = get(LINE_BASE_URL).extract().response();

        // Then
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertCreated(createFirstLineResponse),
            () -> assertCreated(createSecondLineResponse),
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철 목록 정보에서 생성한 지하철 노선 포함 여부 검증")
                .containsAnyOf("신분당선", "분당선")
        );
    }

    /**
     * Given 1개의 지허철 노선을 생성하고 When 지하철 노선을 조회하면 Then 생성한 지하철 노선이 조회된다.
     */
    @Test
    @DisplayName("특정 지하철 노선을 조회한다.")
    public void getLineById() {
        // Given
        Response createLineResponse = generateLine("신분당선", "강남역", "판교역");
        String lineId = getId(createLineResponse);

        // When
        Response response = get(LINE_BASE_URL, lineId).extract().response();

        // Then
        JsonPath jsonPath = response.jsonPath();
        assertAll(
            () -> assertOk(response),
            () -> assertThat(jsonPath.getString("name")).isEqualTo("신분당선"),
            () -> assertThat(jsonPath.getList("stations.name"))
                .containsAnyOf("강남역", "판교역")
        );
    }

    /**
     * Given 1개의 지하철 노선을 생성하고 When 지하철 노선을 수정하면 Then 지하철 노선이 수정된다.
     */
    @Test
    @DisplayName("지하철 노선을 수정한다.")
    public void updateLine() {
        // Given
        Response createLineResponse = generateLine("신분당선", "강남역", "판교역");
        String lineId = getId(createLineResponse);
        UpdateLineRequest updateLineRequest = new UpdateLineRequest("분당선", "bg-yellow-600");

        // When
        Response updateLineResponse = put(LINE_BASE_URL, lineId, updateLineRequest).extract().response();
        Response getLineByIdResponse = get(LINE_BASE_URL, lineId).extract().response();

        // Then
        JsonPath getLineByIdResponseBody = getLineByIdResponse.jsonPath();
        assertAll(
            () -> assertOk(updateLineResponse),
            () -> assertOk(getLineByIdResponse),
            () -> assertThat(getLineByIdResponseBody.getString("name")).isEqualTo(updateLineRequest.getName()),
            () -> assertThat(getLineByIdResponseBody.getString("color")).isEqualTo(updateLineRequest.getColor())
        );
    }

    /**
     * Given 1개의 지하철 노선을 생성하고
     * When 지하철 노선을 삭제하면
     * Then 지하철 노선이 삭제된다.
     */
    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    public void deleteLineById(){
        // Given
        Response createLineResponse = generateLine("신분당선", "강남역", "판교역");
        String lineId = getId(createLineResponse);
    
        // When
        Response deleteLineResponse = delete(LINE_BASE_URL, lineId).extract().response();
        Response getLineByIdResponse = get(LINE_BASE_URL, lineId).extract().response();
    
        // Then
        assertAll(
            () -> assertNoContent(deleteLineResponse),
            () -> assertInternalServerError(getLineByIdResponse)
        );
    }
}
