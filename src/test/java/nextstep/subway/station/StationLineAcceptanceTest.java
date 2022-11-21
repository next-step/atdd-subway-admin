package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.request.LineApi;
import nextstep.subway.station.request.StationApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 노선 인수 테스트")
class StationLineAcceptanceTest extends AcceptanceTest {
    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    private void before() {
        upStationId = getId(StationApi.createStation("강남역"));
        downStationId = getId(StationApi.createStation("성수역"));
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선 생성")
    void createStationLine() {
        ExtractableResponse<Response> response = LineApi.createStationLine("신분당선", upStationId, downStationId, 10);
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> lineNames = getList(LineApi.getStationLines(), "name");
        assertThat(lineNames).contains("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록 조회")
    void getStationLines() {
        LineApi.createStationLine("신분당선", upStationId, downStationId, 10);
        LineApi.createStationLine("분당선", upStationId, downStationId, 10);

        ExtractableResponse<Response> response = LineApi.getStationLines();
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> lineNames = getList(response, "name");
        assertThat(lineNames).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 조회")
    void getStationLine() {
        Long lineId = getId(LineApi.createStationLine("신분당선", upStationId, downStationId, 10));

        ExtractableResponse<Response> response = LineApi.getStationLine(lineId);
        assertThat(response.statusCode()).isEqualTo(200);

        String name = getProperty(response, "name");
        assertThat(name).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선 수정")
    void updateStationLine() {
        Long lineId = getId(LineApi.createStationLine("신분당선", upStationId, downStationId, 10));

        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("name", "분당선");
        updateParams.put("color", "bg-red-600");

        ExtractableResponse<Response> response = LineApi.updateStationLine(lineId, updateParams);
        assertThat(response.statusCode()).isEqualTo(200);

        String lineName = getProperty(LineApi.getStationLine(lineId), "name");
        String color = getProperty(LineApi.getStationLine(lineId), "color");
        assertThat(lineName).isEqualTo("분당선");
        assertThat(color).isEqualTo("bg-red-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선 삭제")
    void deleteStationLine() {
        Long lineId = getId(LineApi.createStationLine("신분당선", upStationId, downStationId, 10));

        ExtractableResponse<Response> response = LineApi.deleteStationLine(lineId);
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> lineNames = getList(LineApi.getStationLines(), "name");
        assertThat(lineNames).isEmpty();
    }
}
