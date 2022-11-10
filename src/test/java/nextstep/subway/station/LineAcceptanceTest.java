package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.util.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * Given 두개의 지하철 역이 등록되어 있을 때
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    public void 지하철노선_생성() {
        List<Long> stationIds = 두개의_지하철_역이_등록되어_있음();

        지하철_노선을_생성한다("노선이름", "bg-red-600", 10, stationIds);

        지하철_노선_목록_조회시_생성한_노선을_찾을_수_있다("노선이름");
    }

    private void 지하철_노선_목록_조회시_생성한_노선을_찾을_수_있다(String name) {
        ExtractableResponse<Response> request = RequestUtil.getRequest("/lines");
        assertThat(request.jsonPath().getString("name")).isEqualTo(name);
    }

    private void 지하철_노선을_생성한다(String lineName, String lineColor, int distance, List<Long> stationIds) {
        Long upStationId = stationIds.get(0);
        Long downStationId = stationIds.get(1);
        LineRequest request = new LineRequest(lineName, lineColor, distance, upStationId, downStationId);
        ExtractableResponse<Response> response = RequestUtil.postRequest("/lines", request);
        assertStatus(response, HttpStatus.CREATED);
    }

    private List<Long> 두개의_지하철_역이_등록되어_있음() {
        지하철역을_생성한다("강남역");
        지하철역을_생성한다("역삼역");
        ExtractableResponse<Response> allStations = 모든_지하철역을_조회한다();
        return allStations.jsonPath().getList("id", Long.class);
    }
}
