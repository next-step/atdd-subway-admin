package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("지하철노선 관련 기능")
@Sql("/sql/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철노선_생성(
                "신분당선",
                "bg-red-600",
                StationAcceptanceTest.지하철역_생성_id_반환("강남역"),
                StationAcceptanceTest.지하철역_생성_id_반환("사당역"),
                10
        );

        // then
        응답결과_확인(response, HttpStatus.CREATED);
        지하철노선_존재함(전체_지하철노선_이름_조회(), "강남역");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철노선_생성(
                "신분당선",
                "bg-red-600",
                StationAcceptanceTest.지하철역_생성_id_반환("강남역"),
                StationAcceptanceTest.지하철역_생성_id_반환("사당역"),
                10
        );
        지하철노선_생성(
                "4호선",
                "blue",
                StationAcceptanceTest.지하철역_생성_id_반환("신용산역"),
                StationAcceptanceTest.지하철역_생성_id_반환("이수역"),
                5
        );

        //when
        ExtractableResponse<Response> response = 전체_지하철노선_조회();

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_개수_같음(response, 2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getStation() {
        // given
        long expected = 지하철노선_생성_id_반환(
                "신분당선",
                "bg-red-600",
                StationAcceptanceTest.지하철역_생성_id_반환("강남역"),
                StationAcceptanceTest.지하철역_생성_id_반환("사당역"),
                10
        );

        //when
        ExtractableResponse<Response> response = 지하철노선_조회(expected);

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_id_같음(response, expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateStation() {
        // given
        long upStationId = StationAcceptanceTest.지하철역_생성_id_반환("강남역");
        long downStationId = StationAcceptanceTest.지하철역_생성_id_반환("사당역");
        long id = 지하철노선_생성_id_반환("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_수정(id, "다른분당선", "bg-red-600");

        // then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_이름_같음(response, "다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        long id = 지하철노선_생성_id_반환(
                "신분당선",
                "bg-red-600",
                StationAcceptanceTest.지하철역_생성_id_반환("강남역"),
                StationAcceptanceTest.지하철역_생성_id_반환("사당역"),
                10
        );

        // when
        ExtractableResponse<Response> response = 지하철노선_삭제(id);

        // then
        응답결과_확인(response, HttpStatus.NO_CONTENT);
    }

    private ExtractableResponse<Response> 지하철노선_생성(String name, String color, long upStationId, long downStationId,
                                                   int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private long 지하철노선_생성_id_반환(String name, String color, long upStationId, long downStationId, int distance) {
        return 지하철노선_생성(name, color, upStationId, downStationId, distance)
                .jsonPath()
                .getLong("id");
    }

    private ExtractableResponse<Response> 지하철노선_수정(long id, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 전체_지하철노선_조회() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회(long id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private List<String> 전체_지하철노선_이름_조회() {
        return 전체_지하철노선_조회()
                .jsonPath()
                .getList("stations.name", String.class);
    }

    private ExtractableResponse<Response> 지하철노선_삭제(long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private void 지하철노선_개수_같음(ExtractableResponse<Response> response, int size) {
        assertThat(response.jsonPath().getList(".").size()).isEqualTo(size);
    }

    private void 지하철노선_id_같음(ExtractableResponse<Response> response, long expected) {
        assertThat(response.jsonPath().getLong("id")).isEqualTo(expected);
    }

    private void 지하철노선_이름_같음(ExtractableResponse<Response> response, String expected) {
        assertThat(response.jsonPath().getString("name")).isEqualTo(expected);
    }

    private void 지하철노선_존재함(List<String> lineNames, String searchName) {
        assertThat(lineNames).containsAnyOf(searchName);
    }

    private void 응답결과_확인(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }
}
