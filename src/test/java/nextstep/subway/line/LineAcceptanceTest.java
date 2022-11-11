package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

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

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
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
     * <p>
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        노선_생성("신분당선", "bg-red-600", "1", "2", "10");

        //then
        List<String> lineNames = 노선_목록조회("name");

        //then
        assertThat(lineNames).contains("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * <p>
     * When 지하철 노선 목록을 조회하면
     * <p>
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        노선_생성("신분당선", "bg-red-600", "1", "2", "10");
        노선_생성("분당선", "bg-green-600", "1", "3", "10");

        //when
        List<String> lineNames = 노선_목록조회("name");

        //then
        assertThat(lineNames).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 조회하면
     * <p>
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLineByName() {
        //given
        노선_생성("신분당선", "bg-red-600", "1", "2", "10");

        //when
        String lineName = 노선_조회("신분당선", "name");

        //then
        assertThat(lineName).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 수정하면
     * <p>
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void modifyLine() {
        //given
        노선_생성("신분당선", "bg-red-600", "1", "2", "10");

        //when
        ExtractableResponse<Response> response = 노선_수정("신분당선", "bg-green-600", "1", "2", "10");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 삭제하면
     * <p>
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 정보를 제거한다.")
    @Test
    void deleteLine() {
        //given
        String id = 노선_생성_값_리턴("신분당선", "bg-red-600", "1", "2", "10", "id");

        //when
        ExtractableResponse<Response> response = 노선_삭제(id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노선_생성(String name, String color, String upStationId, String downStationId,
                                                String distance) {
        Map<String, String> params = createLineMap(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private String 노선_생성_값_리턴(String name, String color, String upStationId, String downStationId,
                              String distance, String value) {
        return 노선_생성(name, color, upStationId, downStationId, distance).jsonPath().getString(value);
    }

    private List<String> 노선_목록조회(String information) {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList(information, String.class);
    }

    private String 노선_조회(String name, String information) {
        return RestAssured.given().log().all()
                .when().get("/lines/{name}", name)
                .then().log().all()
                .extract().jsonPath().getString(information);
    }

    private ExtractableResponse<Response> 노선_수정(String name, String color, String upStationId, String downStationId,
                                                String distance) {
        Map<String, String> params = createLineMap(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{name}", name)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_삭제(String id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private Map<String, String> createLineMap(String name, String color, String upStationId, String downStationId,
                                              String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
