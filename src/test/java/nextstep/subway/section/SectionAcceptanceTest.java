package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.utils.RestAssuredMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
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
        String upStationId = Integer.toString(지하철역_생성("상행종점").jsonPath().get("id"));

        구간_생성(upStationId, lineUpStationId, "5");

        // then
        List<HashMap<String, ?>> stations = 지하철노선_한개_조회(Integer.valueOf(lineId)).get("stations");

        assertAll(
                () -> assertThat(stations.get(0).get("name")).isEqualTo("상행종점"),
                () -> assertThat(
                        stations.stream()
                                .map(target -> target.get("id").toString())
                                .collect(Collectors.toList())
                ).contains(lineUpStationId)
        );
    }

    /**
     * Given 상행과 하행을 생성하고
     * <p>
     * When 새로운 역을 하행 종점으로 등록하면
     * <p>
     * Then 라인 조회 시 하행 종점이 변경된 역 목록을 찾을 수 있다.
     * <p>
     * Then 기존의 하행 종점은 노선의 일반 역으로 변경된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createNewDownStationSection() {
        // when
        String downStationId = Integer.toString(지하철역_생성("하행종점").jsonPath().get("id"));

        구간_생성(lineDownStationId, downStationId, "5");

        // then
        List<HashMap<String, ?>> stations = 지하철노선_한개_조회(Integer.valueOf(lineId)).get("stations");

        assertAll(
                () -> assertThat(stations.get(stations.size() - 1).get("name")).isEqualTo("하행종점"),
                () -> assertThat(
                        stations.stream()
                                .map(target -> target.get("id").toString())
                                .collect(Collectors.toList())
                ).contains(lineDownStationId)
        );
    }

    /**
     * Given 상행과 하행을 생성하고
     * <p>
     * When 기존 역 사이 길이보다 크거나 같은 길이로 등록을 시도하면
     * <p>
     * Then 라인 예외가 발생한다.
     */
    @DisplayName("기존 역 사이 길이보다 크거나 같은 길이로 등록하려 하면 예외가 발생한다.")
    @Test
    void createLongerThrowException() {
        // when
        ExtractableResponse<Response> response = 상행_기준_구간_생성("구간역1", "11");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 상행과 하행을 생성하고
     * <p>
     * When 이미 등록된 역을 구간으로 등록하려고 하면
     * <p>
     * Then 예외가 발생한다.
     */
    @DisplayName("이미 등록된 역을 구간으로 등록하려 하면 예외가 발생한다.")
    @Test
    void createAlreadySectionThrowException() {
        // given
        String stationId = Integer.toString(지하철역_생성("중간역").jsonPath().get("id"));

        // when
        구간_생성(stationId, lineDownStationId, "5");

        // then

        assertAll(
                () -> assertThat(구간_생성(lineUpStationId, lineDownStationId, "7").statusCode()).isEqualTo(
                        HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(구간_생성(stationId, lineDownStationId, "3").statusCode()).isEqualTo(
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    /**
     * Given 상행과 하행을 생성하고
     * <p>
     * When 상행역 혹은 하행역을 포함하지 않고, 구간으로 등록하려고 하면
     * <p>
     * Then 예외가 발생한다.
     */
    @DisplayName("상행역 혹은 하행역을 포함하지 않고, 구간으로 등록하려고 하면 예외가 발생한다.")
    @Test
    void createNotExistedStation() {
        // given
        String notExistedStationId1 = Integer.toString(지하철역_생성("없는역1").jsonPath().get("id"));
        String notExistedStationId2 = Integer.toString(지하철역_생성("없는역2").jsonPath().get("id"));

        // when
        ExtractableResponse<Response> response = 구간_생성(notExistedStationId1, notExistedStationId2, "5");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 구간을 추가하고
     * <p>
     * When 생성된 구간의 역을 제거하면
     * <p>
     * Then 제거한 역을 찾을 수 없다.
     */
    @DisplayName("지하철 노선의 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        String stationId = Integer.toString(지하철역_생성("구간역").jsonPath().get("id"));
        구간_생성(lineUpStationId, stationId, "5");

        // when
        ExtractableResponse<Response> response = RestAssuredMethod.delete("/lines/{id}/sections",
                new HashMap<String, String>() {{
                    put("id", lineId);
                }}, new HashMap<String, String>() {{
                    put("stationId", stationId);
                }});

        // then
        List<HashMap<String, ?>> stations = 지하철노선_한개_조회(Integer.valueOf(lineId)).get("stations");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(
                        stations.stream()
                                .map(target -> target.get("id").toString())
                                .collect(Collectors.toList())
                ).doesNotContain(stationId),
                () -> assertThat(stations.size()).isEqualTo(2)
        );
    }

    /**
     * Given 지하철 노선에 구간을 추가하고
     * <p>
     * When 구간의 상행 종점을 제거하면
     * <p>
     * Then 그 다음 역이 상행 종점으로 변경된다
     */
    @DisplayName("지하철 노선의 상행 종점을 제거한다.")
    @Test
    void deleteUpStationSection() {
        // given
        String stationId = Integer.toString(지하철역_생성("구간역").jsonPath().get("id"));
        구간_생성(lineUpStationId, stationId, "5");

        // when
        ExtractableResponse<Response> response = RestAssuredMethod.delete("/lines/{id}/sections",
                new HashMap<String, String>() {{
                    put("id", lineId);
                }}, new HashMap<String, String>() {{
                    put("stationId", lineUpStationId);
                }});

        // then
        List<HashMap<String, ?>> stations = 지하철노선_한개_조회(Integer.valueOf(lineId)).get("stations");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(
                        stations.stream()
                                .map(target -> target.get("id").toString())
                                .collect(Collectors.toList())
                ).doesNotContain(lineUpStationId),
                () -> assertThat(stations.size()).isEqualTo(2)
        );
    }

    /**
     * Given 지하철 노선에 구간을 추가하고
     * <p>
     * When 구간의 하행 종점을 제거하면
     * <p>
     * Then 그 전 역이 하행 종점으로 변경된다
     */
    @DisplayName("지하철 노선의 상행 종점을 제거한다.")
    @Test
    void deleteDownStationSection() {
        // given
        String stationId = Integer.toString(지하철역_생성("구간역").jsonPath().get("id"));
        구간_생성(lineUpStationId, stationId, "5");

        // when
        ExtractableResponse<Response> response = RestAssuredMethod.delete("/lines/{id}/sections",
                new HashMap<String, String>() {{
                    put("id", lineId);
                }}, new HashMap<String, String>() {{
                    put("stationId", lineDownStationId);
                }});

        // then
        List<HashMap<String, ?>> stations = 지하철노선_한개_조회(Integer.valueOf(lineId)).get("stations");
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(
                        stations.stream()
                                .map(target -> target.get("id").toString())
                                .collect(Collectors.toList())
                ).doesNotContain(lineDownStationId),
                () -> assertThat(stations.size()).isEqualTo(2)
        );
    }

    /**
     * When 노선에 등록되어 있지 않은 역을 제거하려고 하면
     * <p>
     * Then 예외가 발생한다.
     */
    @DisplayName("노선에 등록되어 있지 않은 역을 제거하려고 하면 예외 발생")
    @Test
    void deleteNotExistedSection() {

        // when
        ExtractableResponse<Response> response = RestAssuredMethod.delete("/lines/{id}/sections",
                new HashMap<String, String>() {{
                    put("id", lineId);
                }}, new HashMap<String, Integer>() {{
                    put("stationId", 지하철역_생성("없는역").jsonPath().get("id"));
                }});

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 구간이 하나 밖에 없고
     * <p>
     * When 상행 종점을 제거하려 하면
     * <p>
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 구간이 하나 밖에 없고 상행 종점을 제거하려고 하면 예외 발생")
    @Test
    void deleteLastUpStationSection() {

        // when
        ExtractableResponse<Response> response = RestAssuredMethod.delete("/lines/{id}/sections",
                new HashMap<String, String>() {{
                    put("id", lineId);
                }}, new HashMap<String, String>() {{
                    put("stationId", lineUpStationId);
                }});

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 구간이 하나 밖에 없고
     * <p>
     * When 하행 종점을 제거하려 하면
     * <p>
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 구간이 하나 밖에 없고 하행 종점을 제거하려고 하면 예외 발생")
    @Test
    void deleteLastDownStationSection() {

        // when
        ExtractableResponse<Response> response = RestAssuredMethod.delete("/lines/{id}/sections",
                new HashMap<String, String>() {{
                    put("id", lineId);
                }}, new HashMap<String, String>() {{
                    put("stationId", lineDownStationId);
                }});

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 구간_생성(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("distance", distance);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        return RestAssuredMethod.post("/lines/{id}/sections", params,
                new HashMap<String, String>() {{
                    put("id", lineId);
                }});
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
