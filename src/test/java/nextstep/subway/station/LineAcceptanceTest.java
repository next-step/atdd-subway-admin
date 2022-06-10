package nextstep.subway.station;

import static nextstep.subway.utils.AssertionsUtils.assertCreated;
import static nextstep.subway.utils.AssertionsUtils.assertOk;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getId;
import static nextstep.subway.utils.RestAssuredUtils.get;
import static nextstep.subway.utils.RestAssuredUtils.post;
import static nextstep.subway.utils.StationsAcceptanceUtils.generateStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nextstep.subway.dto.line.CreateLineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    public static final String LINE_BASE_URL = "/lines";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 목록 정보에서 생성한 지하철 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    public void createLine() {
        // Given
        final String upStationName = "강남역";
        final String downStationName = "판교역";
        Response upStation = generateStation(upStationName);
        Response downStation = generateStation(downStationName);
        final CreateLineRequest createLineRequest = new CreateLineRequest(
            "신분당선",
            "bg-red-600",
            Long.parseLong(getId(upStation)),
            Long.parseLong(getId(downStation)),
            10
        );

        // When
        Response createLineResponse = post(LINE_BASE_URL, createLineRequest).extract().response();
        Response getAllStationsResponse = get(LINE_BASE_URL).extract().response();

        // Then
        JsonPath createLineResponseBody = createLineResponse.jsonPath();
        JsonPath getAllStationsResponseBody = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertCreated(createLineResponse),
            () -> assertThat(createLineResponseBody.getString("name")).isEqualTo(createLineRequest.getName()),
            () -> assertThat(createLineResponseBody.getList("stations.name"))
                .containsAnyOf(upStationName, downStationName),
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(getAllStationsResponseBody.getList("name"))
                .as("지하철 목록 정보에서 생성한 지하철 노선 포함 여부 검증")
                .containsAnyOf(createLineRequest.getName())
        );
    }
}
