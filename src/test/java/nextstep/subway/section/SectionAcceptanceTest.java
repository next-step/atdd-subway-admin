package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.utils.RestAssuredMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("지하철 구간 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    String lineUpStationId;
    String lineDownStationId;

    String lineId;


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        lineUpStationId = Integer.toString(지하철역_생성("강남역").jsonPath().get("id"));
        lineDownStationId = Integer.toString(지하철역_생성("선릉역").jsonPath().get("id"));

        lineId = Integer.toString(
                지하철노선_생성("신분당선", "bg-red-600", lineUpStationId, lineDownStationId, "10").jsonPath().get("id"));
    }

    /**
     * When 역 사이에 새로운 역을 등록하면
     * <p>
     * Then 지하철 노선의 구간 목록 조회 시 추가한 구간을 찾을 수 있다.
     */
    @DisplayName("노선의 구간을 생성한다.")
    @Test
    void createSection() {
        // when
        상행_기준_구간_생성("구간역1", "5");
        하행_기준_구간_생성("구간역2", "3");

        // then
        List<HashMap<String, ?>> stations = 지하철노선_한개_조회(Integer.valueOf(lineId)).get("stations");
        assertThat(
                stations.stream()
                        .map(target -> target.get("name").toString())
                        .collect(Collectors.toList())
        ).contains("구간역1", "구간역2");
    }

    /**
     * Given 상행과 하행을 생성하고
     * <p>
     * When 새로운 역을 상행 종점으로 등록하면
     * <p>
     * Then 라인 조회 시 상행 종점이 변경된 역 목록을 찾을 수 있다.
     * <p>
     * Then 기존의 상행 종점은 노선의 일반 역으로 변경된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createNewUpStationSection() {
        // when
        String upStationId = Integer.toString(지하철역_생성("구간역1").jsonPath().get("id"));

        Map<String, String> params = new HashMap<>();
        params.put("distance", "5");
        params.put("upStationId", upStationId);
        params.put("downStationId", lineUpStationId);
        RestAssuredMethod.post("/lines/{id}/sections", params,
                new HashMap<String, String>() {{
                    put("id", lineId);
                }});

        // then
        List<HashMap<String, ?>> stations = 지하철노선_한개_조회(Integer.valueOf(lineId)).get("stations");

        assertThat(stations.get(0).get("name")).isEqualTo("구간역1");
        assertThat(
                stations.stream()
                        .map(target -> target.get("id").toString())
                        .collect(Collectors.toList())
        ).contains(lineUpStationId);
    }


    private ExtractableResponse<Response> 상행_기준_구간_생성(String stationName, String distance) {
        String downStationId = Integer.toString(지하철역_생성(stationName).jsonPath().get("id"));

        Map<String, String> params = new HashMap<>();
        params.put("distance", distance);
        params.put("upStationId", lineUpStationId);
        params.put("downStationId", downStationId);
        return RestAssuredMethod.post("/lines/{id}/sections", params,
                new HashMap<String, String>() {{
                    put("id", lineId);
                }});
    }

    private ExtractableResponse<Response> 하행_기준_구간_생성(String stationName, String distance) {
        String upStationId = Integer.toString(지하철역_생성(stationName).jsonPath().get("id"));

        Map<String, String> params = new HashMap<>();
        params.put("distance", distance);
        params.put("upStationId", upStationId);
        params.put("downStationId", lineDownStationId);
        return RestAssuredMethod.post("/lines/{id}/sections", params,
                new HashMap<String, String>() {{
                    put("id", lineId);
                }});
    }


    private ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssuredMethod.post("/stations", params);
    }

    private ExtractableResponse<Response> 지하철노선_생성(String name, String color, String upStationId, String downStationId,
                                                   String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssuredMethod.post("/lines", params);
    }

    private JsonPath 지하철노선_한개_조회(Integer id) {
        return RestAssuredMethod.get("/lines/{id}", new HashMap<String, Integer>() {{
            put("id", id);
        }}).jsonPath();
    }
}
