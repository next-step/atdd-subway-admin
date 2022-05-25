package nextstep.subway.lines;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.helper.RequestHelper;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 api 인수 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LinesAcceptanceTest {
    private final String LINE_PATH = "/lines";
    private final String STATION_PATH = "/stations";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLinesTest() {
        // given
        Long upStationId = RequestHelper
                .postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "지하철역"))
                .jsonPath()
                .getLong("id");
        Long downStationId = RequestHelper
                .postRequest(STATION_PATH, new HashMap<>(), Collections.singletonMap("name", "새로운지하철역"))
                .jsonPath()
                .getLong("id");
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

//    /**
//     * Given 2개의 지하철 노선을 생성하고
//     * When 지하철 노선 목록을 조회하면
//     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
//     */
//    @DisplayName("지하철 노선 목록을 조회한다.")
//    @Test
//    void getAllLinesTest() {
//        // given
//        LineRequest lineRequest1 = new LineRequest("신분당선");
//        LineRequest lineRequest2 = new LineRequest("수인분당선");
//
//        // when
//        RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest1);
//        RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest2);
//        List<String> lineNames = RequestHelper.getRequest(LINE_PATH, new HashMap<>())
//                .jsonPath()
//                .getList("name", String.class);
//
//        // then
//        assertThat(lineNames).containsAnyOf(lineRequest1.getName(), lineRequest2.getName());
//    }
//
//    /**
//     * Given 지하철 노선을 생성하고
//     * When 생성한 지하철 노선을 조회하면
//     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
//     */
//    @DisplayName("지하철 노선을 조회한다.")
//    @Test
//    void getLinesTest() {
//        // given
//        Map<String, String> stationRequest1 = Collections.singletonMap("name", "강남역");
//        Map<String, String> stationRequest2 = Collections.singletonMap("name", "판교역");
//        LineRequest lineRequest = new LineRequest("신분당선", Arrays.asList(stationRequest1, stationRequest2));
//
//        // when
//        String createdLineId = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest)
//                .jsonPath()
//                .getString("id");
//        ExtractableResponse<Response> response = RequestHelper
//                .getRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLineId);
//        List<String> stationNames = response.jsonPath()
//                .getList("stations[*].name", String.class);
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
//        assertThat(stationNames).containsAll(Arrays.asList(stationRequest1.get("name"), stationRequest1.get("name")));
//    }
//
//    /**
//     * Given 지하철 노선을 생성하고
//     * When 생성한 지하철 노선을 수정하면
//     * Then 해당 지하철 노선 정보는 수정된다
//     */
//    @DisplayName("지하철 노선을 수정한다.")
//    @Test
//    void updateLinesTest() {
//        // given
//        LineRequest lineRequest = new LineRequest("신분당선");
//        LineRequest tobeLineRequest = new LineRequest("다른분당선");
//        String createdLineId = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest)
//                .jsonPath()
//                .getString("id");
//
//        // when
//        ExtractableResponse<Response> response = RequestHelper
//                .putRequest(LINE_PATH + "/{id}", new HashMap<>(), tobeLineRequest, createdLineId);
//        String changedName = RequestHelper.getRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLineId)
//                .jsonPath()
//                .get("name");
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
//        assertThat(changedName).isNotEqualTo(lineRequest.getName());
//        assertThat(changedName).isEqualTo(tobeLineRequest.getName());
//    }
//
//    /**
//     * Given 지하철 노선을 생성하고
//     * When 생성한 지하철 노선을 삭제하면
//     * Then 해당 지하철 노선 정보는 삭제된다
//     */
//    @DisplayName("지하철 노선을 삭제한다.")
//    @Test
//    void deleteLinesTest() {
//        // given
//        String createdLineId = RequestHelper.postRequest(LINE_PATH, new HashMap<>(), lineRequest)
//                .jsonPath()
//                .getString("id");
//
//        // when
//        ExtractableResponse<Response> deleteResponse = RequestHelper
//                .deleteRequest(LINE_PATH + "/{id}", new HashMap<>(), createdLineId);
//        List<String> lineIds = RequestHelper.getRequest(LINE_PATH, new HashMap<>())
//                .jsonPath()
//                .getList("id", String.class);
//
//        // then
//        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
//        assertThat(lineIds).doesNotContain(createdLineId);
//    }

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
}
