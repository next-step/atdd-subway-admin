package nextstep.subway.Line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
//@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    private final String DELIMITER = "/";

    @Autowired
    StationRepository stationRepository;

    private static Map<String, String> of(String lineName, String color) {
        Map<String, String> map = new HashMap<>();
        map.put("name", lineName);
        map.put("color", color);
        return map;
    }

    @LocalServerPort
    private int port;

    private Map<String, String> params;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        params = LineAcceptanceTest.of("2호선", "green");
        creatStation("강남역");
        creatStation("서초역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLine(params);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> lineNames =
                RestAssured.given().log().all()
                        .when().get("/lines")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        // then
        assertThat(lineNames).containsAnyOf("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회.")
    @Test
    void showCreatedLines() {
        // given
        Map<String, String> params1 = LineAcceptanceTest.of("2호선", "green");
        Map<String, String> params2 = LineAcceptanceTest.of("3호선", "orange");
        ExtractableResponse<Response> response1 = createLine(params1);
        ExtractableResponse<Response> response2 = createLine(params2);


        assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        List<String> lineNames =
                RestAssured.given().log().all()
                        .when().get("/lines")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        // then
        assertThat(lineNames).hasSize(2);

    }
    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 특정 노선 조회.")
    @Test
    void retrieveTheLine() {
        // given
        ExtractableResponse<Response> response = createLine(params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        ExtractableResponse<Response> retrieveResponse =
                RestAssured.given().log().all()
                        .when().get("/lines" + DELIMITER + response.body().jsonPath().getLong("id"))
                        .then().log().all()
                        .extract();
        // then
        assertThat(retrieveResponse.body().jsonPath().getString("name")).contains("2호선");
    }
    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 특정 노선 수정.")
    @Test
    void modifyTheLine() {
        // given
        ExtractableResponse<Response> response = createLine(params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        Map<String, String> modifyParams = LineAcceptanceTest.of("3호선", "orange");
        ExtractableResponse<Response> modifyResponse =
                RestAssured.given().log().all()
                        .body(modifyParams)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put("/lines" + DELIMITER + response.body().jsonPath().getLong("id"))
                        .then().log().all()
                        .extract();
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        // then
        ExtractableResponse<Response> retrieveResponse =
                RestAssured.given().log().all()
                        .when().get("/lines" + DELIMITER + response.body().jsonPath().getLong("id"))
                        .then().log().all()
                        .extract();
        assertThat(retrieveResponse.body().jsonPath().getString("name")).contains("3호선");
    }
    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 특정 노선 삭제.")
    @Test
    void deleteTheLine() {
        // given
        ExtractableResponse<Response> response = createLine(params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // when
        ExtractableResponse<Response> modifyResponse =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete("/lines" + DELIMITER + response.body().jsonPath().getLong("id"))
                        .then().log().all()
                        .extract();
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        // then
        ExtractableResponse<Response> retrieveResponse =
                RestAssured.given().log().all()
                        .when().get("/lines" + DELIMITER + response.body().jsonPath().getLong("id"))
                        .then().log().all()
                        .extract();
        assertThat(retrieveResponse.body().jsonPath().getString("name")).isNull();
    }

    private ExtractableResponse<Response> createLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    @Transactional
    public void creatStation(String name) {
        stationRepository.save(new StationRequest(name).toStation());
    }
}