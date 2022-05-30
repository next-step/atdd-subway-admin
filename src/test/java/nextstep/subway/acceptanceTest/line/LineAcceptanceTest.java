package nextstep.subway.acceptanceTest.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련기능 인수테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts={"classpath:/db/truncate.sql", "classpath:/db/data.sql"})
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * Given 지하철역을 생성하고
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선을 생성한다")
    void createLine() {
        //when
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", "1", "2", "10");

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(getLines("name")).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선을 두 개 생성하고 목록을 조회한다")
    void findAll() {
        //given
        createLine("신분당선", "bg-red-600", "1", "2", "10");
        createLine("분당선", "bg-green-600", "2", "3", "5");

        // when
        List<String> lines = getLines("name");

        // then
        assertThat(lines).hasSize(2).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("생성한 지하철 노선을 조회한다")
    void findById() {
        //given
        createLine("신분당선", "bg-red-600", "1", "2", "10");

        //when
        List<String> lines = getLines("name");

        //then
        assertThat(lines).contains("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성하지 않은 지하철 노선을 조회하면
     * Then 404를 응답받는다.
     */
    @Test
    @DisplayName("생성하지 않은 지하철을 조회한다")
    void notFound() {
        //given
        createLine("신분당선", "bg-red-600", "1", "2", "10");

        //when
        int statusCode = RestAssured.given().log().all()
                .when().get("/lines/2")
                .then().log().all()
                .extract().statusCode();

        //then
        assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선정보를 수정한다")
    void changeLine() {
        //given
        createLine("신분당선", "bg-red-600", "1", "2", "10");

        //when
        Map<String, String> params = new HashMap<>();
        params.put("name", "천당 위에 분당선");
        params.put("color", "bg-god-7000");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/1")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선을 제거한다")
    void deleteLine() {
        //given
        ExtractableResponse<Response> line =
                createLine("신분당선", "bg-red-600", "1", "2", "10");
        int id = line.jsonPath().get("id");


        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getLines("name")).doesNotContain("신분당선").hasSize(0);
    }

    private ExtractableResponse<Response> createLine(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().log().all()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private List<String> getLines(String jsonPath) {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList(jsonPath, String.class);
    }
}
