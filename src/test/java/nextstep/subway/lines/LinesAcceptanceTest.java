package nextstep.subway.lines;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.helper.DatabaseCleanup;
import nextstep.subway.helper.RequestHelper;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 api 인수 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
public class LinesAcceptanceTest {
    private final String LINE_PATH = "/lines";
    private final String STATION_PATH = "/stations";

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLinesTest() {
        // given
        Long upStationId = Long.parseLong(saveStationAndGetInfo("지하철역").get("id"));
        Long downStationId = Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id"));
        Map<String, Object> lineRequest = createLineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10L);

        // when
        ExtractableResponse<Response> response = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest);
        List<String> lineNames = RequestHelper.getRequest(LINE_PATH, new HashMap<>())
                .jsonPath()
                .getList("name", String.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getAllLinesTest() {
        // given
        List<Long> stationIds = Arrays.asList(
                Long.parseLong(saveStationAndGetInfo("지하철역").get("id")),
                Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id")),
                Long.parseLong(saveStationAndGetInfo("또다른지하철역").get("id"))
        );
        RequestHelper.postRequest(
                LINE_PATH,
                new HashMap<>(),
                createLineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)
        );
        RequestHelper.postRequest(
                LINE_PATH,
                new HashMap<>(),
                createLineRequest("분당선", "bg-green-600", stationIds.get(1), stationIds.get(2), 10L)
        );

        // when
        List<String> lineNames = RequestHelper.getRequest(LINE_PATH, new HashMap<>())
                .jsonPath()
                .getList("name", String.class);


        // then
        assertThat(lineNames).containsAnyOf("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLinesTest() {
        // given
        List<Long> stationIds = Arrays.asList(
                Long.parseLong(saveStationAndGetInfo("지하철역").get("id")),
                Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id"))
        );
        String createdLineId = RequestHelper.postRequest(
                LINE_PATH,
                new HashMap<>(),
                createLineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)
        ).jsonPath()
                .getString("id");

        // when
        ExtractableResponse<Response> response = RequestHelper
                .getRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLineId);
        List<String> stationNames = response.jsonPath()
                .getList("stations.name", String.class);
        String lineName = response.jsonPath()
                .get("name");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(stationNames).containsAll(Arrays.asList("지하철역", "새로운지하철역"));
        assertThat(lineName).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLinesTest() {
        // given
        String createdLineId = RequestHelper.postRequest(
                LINE_PATH,
                new HashMap<>(),
                createLineRequest("신분당선", "bg-red-600", null, null, 10L)
        ).jsonPath()
                .getString("id");
        Map<String, Object> updateRequest = updateLineRequest("다른분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = RequestHelper
                .putRequest(LINE_PATH + "/{id}", new HashMap<>(), updateRequest, createdLineId);
        String changedName = RequestHelper.getRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLineId)
                .jsonPath()
                .get("name");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(changedName).isNotEqualTo("신분당선");
        assertThat(changedName).isEqualTo("다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLinesTest() {
        // given
        String createdLineId = RequestHelper.postRequest(
                LINE_PATH,
                new HashMap<>(),
                createLineRequest("신분당선", "bg-red-600", null, null, 10L)
        ).jsonPath()
                .getString("id");

        // when
        ExtractableResponse<Response> deleteResponse = RequestHelper
                .deleteRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLineId);
        List<String> lineIds = RequestHelper.getRequest(LINE_PATH, new HashMap<>())
                .jsonPath()
                .getList("id", String.class);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(lineIds).doesNotContain(createdLineId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선 중간에 지하철을 추가하면
     * Then 해당 지하철 노선 정보가 추가된다
     */
    @DisplayName("기존 지하철 노선에 중간 노선을 추가한다")
    @Test
    void addMiddleSection() {
        // given
        Long upStationId = Long.parseLong(saveStationAndGetInfo("지하철역").get("id"));
        Long downStationId = Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id"));
        Long middleStationId = Long.parseLong(saveStationAndGetInfo("또다른지하철역").get("id"));
        Map<String, Object> lineRequest = createLineRequest(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );
        Map<String, Object> addMiddleSectionLineRequest = createLineRequest(
                null, null, upStationId, middleStationId, 4L
        );
        Long createdLineId = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest)
                .jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> addLineResponse = RequestHelper
                .postRequest(LINE_PATH + "/{id}/sections", new HashMap<>(), addMiddleSectionLineRequest, createdLineId);
        ExtractableResponse<Response> originalCreationLine = RequestHelper
                .getRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLineId);

        // then
        assertThat(addLineResponse.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(originalCreationLine.jsonPath().getList("stations.id", Long.class))
                .containsExactly(middleStationId, downStationId);
        assertThat(addLineResponse.jsonPath().getList("stations.id", Long.class))
                .containsExactly(upStationId, middleStationId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 하행 종점, 상행 종점을 추가하면
     * Then 해당 지하철 노선 정보가 추가된다
     */
    @DisplayName("기존 지하철 노선에 하행 종점, 상행 종점을 추가한다")
    @Test
    void addFirstOrLastSection() {
        // given
        Long upStationId = Long.parseLong(saveStationAndGetInfo("지하철역").get("id"));
        Long downStationId = Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id"));
        Long newFirstStationId = Long.parseLong(saveStationAndGetInfo("다른지하철역").get("id"));
        Long newLastStationId = Long.parseLong(saveStationAndGetInfo("또다른지하철역").get("id"));
        Map<String, Object> lineRequest1 = createLineRequest(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );
        Map<String, Object> lineRequest2 = createLineRequest(
                "분당선", "bg-red-600", upStationId, downStationId, 10L
        );
        Map<String, Object> newFirstLineRequest = createLineRequest(
                null, null, newFirstStationId, upStationId, 4L
        );
        Map<String, Object> newLastLineRequest = createLineRequest(
                null, null, downStationId, newLastStationId, 4L
        );
        Long createdLine1Id = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest1)
                .jsonPath()
                .getLong("id");
        Long createdLine2Id = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest2)
                .jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> newFirstResponse = RequestHelper
                .postRequest(LINE_PATH + "/{id}/sections", new HashMap<>(), newFirstLineRequest, createdLine1Id);
        ExtractableResponse<Response> newLastResponse = RequestHelper
                .postRequest(LINE_PATH + "/{id}/sections", new HashMap<>(), newLastLineRequest, createdLine2Id);
        ExtractableResponse<Response> originalCreationLine1 = RequestHelper
                .getRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLine1Id);
        ExtractableResponse<Response> originalCreationLine2 = RequestHelper
                .getRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLine2Id);

        // then
        assertThat(newFirstResponse.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(newLastResponse.statusCode()).isEqualTo(HttpStatus.SC_CREATED);

        assertThat(originalCreationLine1.jsonPath().getList("stations.id", Long.class))
                .containsAnyOf(upStationId, downStationId);
        assertThat(newFirstResponse.jsonPath().getList("stations.id", Long.class))
                .containsAnyOf(newFirstStationId, upStationId);

        assertThat(originalCreationLine2.jsonPath().getList("stations.id", Long.class))
                .containsAnyOf(upStationId, downStationId);
        assertThat(originalCreationLine2.jsonPath().getList("stations.id", Long.class))
                .containsAnyOf(downStationId, newLastStationId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선 중간에 원본 노선의 길이보다 크거나 같은 노선을 추가하면
     * Then 해당 지하철 노선 추가가 실패된다
     */
    @DisplayName("기존 지하철 노선의 길이보다 크거나 같은 중간 노선을 추가하면 실패된다")
    @Test
    void addMiddleSectionByLongerOrSameDistance() {
        // given
        Long upStationId = Long.parseLong(saveStationAndGetInfo("지하철역").get("id"));
        Long downStationId = Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id"));
        Long middleStationId = Long.parseLong(saveStationAndGetInfo("또다른지하철역").get("id"));
        Map<String, Object> lineRequest = createLineRequest(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );
        Map<String, Object> sameDistanceLineRequest = createLineRequest(
                null, null, upStationId, middleStationId, 10L
        );
        Map<String, Object> longerDistanceLineRequest = createLineRequest(
                null, null, upStationId, middleStationId, 11L
        );
        Long createdLineId = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest)
                .jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> sameDistanceResponse = RequestHelper
                .postRequest(LINE_PATH + "/{id}/sections", new HashMap<>(), sameDistanceLineRequest, createdLineId);
        ExtractableResponse<Response> longerDistanceResponse = RequestHelper
                .postRequest(LINE_PATH + "/{id}/sections", new HashMap<>(), longerDistanceLineRequest, createdLineId);

        // then
        assertThat(sameDistanceResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(longerDistanceResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 해당 노선의 상행, 하행 노선이 같은 노선을 추가하면
     * Then 해당 지하철 노선 추가가 실패된다
     */
    @DisplayName("추가하려는 노선의 상행, 하행 노선이 추가되는 노선의 상행, 하행이 같은 노선을 추가하면 실패한다")
    @Test
    void addSameUpAndDownStationLine() {
        // given
        Long upStationId = Long.parseLong(saveStationAndGetInfo("지하철역").get("id"));
        Long downStationId = Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id"));
        Map<String, Object> lineRequest = createLineRequest(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );
        Map<String, Object> sameLineStation = createLineRequest(
                null, null, upStationId, downStationId, 7L
        );
        Long createdLineId = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest)
                .jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> sameStationLineResponse = RequestHelper
                .postRequest(LINE_PATH + "/{id}" + "/sections", new HashMap<>(), sameLineStation, createdLineId);

        // then
        assertThat(sameStationLineResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 해당 노선의 상행과 하행 노선 모두 같지 않은 노선을 추가하면
     * Then 해당 지하철 노선 추가가 실패된다
     */
    @DisplayName("추가하려는 노선의 상행, 하행 노선이 추가되는 노선의 상행, 하행의 모든 노선과 다른 노선을 추가하면 실패한다")
    @Test
    void addNothingMatchUpAndDownStationLine() {
        // given
        Long upStationId = Long.parseLong(saveStationAndGetInfo("지하철역").get("id"));
        Long downStationId = Long.parseLong(saveStationAndGetInfo("새로운지하철역").get("id"));
        Long newStationId1 = Long.parseLong(saveStationAndGetInfo("다른지하철역").get("id"));
        Long newStationId2 = Long.parseLong(saveStationAndGetInfo("또다른지하철역").get("id"));
        Map<String, Object> lineRequest = createLineRequest(
                "신분당선", "bg-red-600", upStationId, downStationId, 10L
        );
        Map<String, Object> nothingMatchedStationLineRequest = createLineRequest(
                null, null, newStationId1, newStationId2, 7L
        );
        Long createdLineId = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest)
                .jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> sameStationLineResponse = RequestHelper
                .postRequest(LINE_PATH + "/{id}" + "/sections", new HashMap<>(), nothingMatchedStationLineRequest, createdLineId);

        // then
        assertThat(sameStationLineResponse.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    private Map<String, String> saveStationAndGetInfo(String stationName) {
        Map<String, String> result = new HashMap<>();
        ExtractableResponse<Response> stationResponse = RequestHelper
                .postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", stationName));

        result.put("id", stationResponse.jsonPath().getString("id"));
        result.put("name", stationResponse.jsonPath().getString("name"));

        return result;
    }

    private Map<String, Object> createLineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> lineRequest = new HashMap<>();

        if (name != null) {
            lineRequest.put("name", name);
        }
        if (color != null) {
            lineRequest.put("color", color);
        }
        if (upStationId != null) {
            lineRequest.put("upStationId", upStationId);
        }
        if (downStationId != null) {
            lineRequest.put("downStationId", downStationId);
        }
        if (distance != null) {
            lineRequest.put("distance", distance);
        }

        return lineRequest;
    }

    private Map<String, Object> updateLineRequest(String name, String color) {
        Map<String, Object> updateRequest = new HashMap<>();

        if (name != null) {
            updateRequest.put("name", name);
        }
        if (color != null) {
            updateRequest.put("color", color);
        }

        return updateRequest;
    }
}
