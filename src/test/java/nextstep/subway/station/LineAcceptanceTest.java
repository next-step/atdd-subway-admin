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
     * Then 응답에 요청했던 정보가 모두 포함되어 있다.
     */
    @Test
    public void 지하철노선_생성() {
        List<Long> stationIds = 두개의_지하철_역이_등록되어_있음();

        ExtractableResponse<Response> response = 지하철_노선을_생성한다("노선이름", "bg-red-600", 10L, stationIds);

        응답에_요청했던_정보가_모두_포함되어_있다(response,"노선이름","bg-red-600",10L,stationIds);
    }

    private void 응답에_요청했던_정보가_모두_포함되어_있다(ExtractableResponse<Response> response, String 노선이름, String color, long l, List<Long> stationIds) {
        assertThat(response.jsonPath().getLong("id")).isPositive();
        assertThat(response.jsonPath().getString("name")).isEqualTo(노선이름);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(l);
        assertThat(response.jsonPath().getList("stations.id")).contains(stationIds.get(0).intValue(),stationIds.get(1).intValue());

    }

    private void 응답에_요청했던_정보가_모두_포함되어_있다(String name) {
        ExtractableResponse<Response> request = RequestUtil.getRequest("/lines");
        assertThat(request.jsonPath().getString("name")).isEqualTo(name);
    }

    private ExtractableResponse<Response> 지하철_노선을_생성한다(String lineName, String lineColor, Long distance, List<Long> stationIds) {
        Long upStationId = stationIds.get(0);
        Long downStationId = stationIds.get(1);
        LineRequest request = new LineRequest(lineName, lineColor, distance, upStationId, downStationId);
        ExtractableResponse<Response> response = RequestUtil.postRequest("/lines", request);
        assertStatus(response, HttpStatus.CREATED);
        return response;
    }

    private List<Long> 두개의_지하철_역이_등록되어_있음() {
        지하철역을_생성한다("강남역");
        지하철역을_생성한다("역삼역");
        ExtractableResponse<Response> allStations = 모든_지하철역을_조회한다();
        return allStations.jsonPath().getList("id", Long.class);
    }
}
