package nextstep.subway.acceptance;

import static nextstep.subway.utils.AssertionsUtils.assertCreated;
import static nextstep.subway.utils.AssertionsUtils.assertInternalServerError;
import static nextstep.subway.utils.AssertionsUtils.assertNoContent;
import static nextstep.subway.utils.AssertionsUtils.assertOk;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_목록_조회_요청;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_삭제_요청;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_생성_요청;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_수정_요청;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_조회_요청;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getIdAsLong;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nextstep.subway.config.BaseTest;
import nextstep.subway.dto.UpdateLineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 목록 정보에서 생성한 지하철 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    public void createLine() {
        // When
        Response createLineResponse = 지하철_노선_생성_요청("신분당선", "강남역", "판교역");
        Response getAllStationsResponse = 지하철_노선_목록_조회_요청();

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
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 목록 정보를 조회하면
     * Then 생성한 지하철 노선 정보를 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    public void getAllLines() {
        // Given
        지하철_노선_생성_요청("신분당선", "강남역", "판교역");
        지하철_노선_생성_요청("분당선", "문정역", "야탑역");

        // When
        Response getAllStationsResponse = 지하철_노선_목록_조회_요청();

        // Then
        JsonPath jsonPath = getAllStationsResponse.jsonPath();
        assertAll(
            () -> assertOk(getAllStationsResponse),
            () -> assertThat(jsonPath.getList("name"))
                .as("지하철 목록 정보에서 생성한 지하철 노선 포함 여부 검증")
                .containsAnyOf("신분당선", "분당선")
        );
    }

    /**
     * Given 1개의 지허철 노선을 생성하고
     * When 지하철 노선을 조회하면
     * Then 생성한 지하철 노선이 조회된다.
     */
    @Test
    @DisplayName("특정 지하철 노선을 조회한다.")
    public void getLineById() {
        // Given
        Long 신분당선 = getIdAsLong(지하철_노선_생성_요청("신분당선", "강남역", "판교역"));

        // When
        Response response = 지하철_노선_조회_요청(신분당선);

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
     * Given 1개의 지하철 노선을 생성하고
     * When 지하철 노선을 수정하면
     * Then 지하철 노선이 수정된다.
     */
    @Test
    @DisplayName("지하철 노선을 수정한다.")
    public void updateLine() {
        // Given
        Long 신분당선 = getIdAsLong(지하철_노선_생성_요청("신분당선", "강남역", "판교역"));
        UpdateLineRequest updateLineRequest = new UpdateLineRequest("분당선", "bg-yellow-600");

        // When
        Response updateLineResponse = 지하철_노선_수정_요청(신분당선, updateLineRequest);
        Response getLineByIdResponse = 지하철_노선_조회_요청(신분당선);

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
    public void deleteLineById() {
        // Given
        Long 신분당선 = getIdAsLong(지하철_노선_생성_요청("신분당선", "강남역", "판교역"));

        // When
        Response deleteLineResponse = 지하철_노선_삭제_요청(신분당선);
        Response getLineByIdResponse = 지하철_노선_조회_요청(신분당선);

        // Then
        assertAll(
            () -> assertNoContent(deleteLineResponse),
            () -> assertInternalServerError(getLineByIdResponse)
        );
    }
}
