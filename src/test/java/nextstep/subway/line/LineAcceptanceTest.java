package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
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
    void createLine() {
        createLine(LineRequest.of("신분당선", "red", 1L, 2L, 10));

        JsonPath jsonPath = showAllLines().body().jsonPath();
        Assertions.assertThat(jsonPath.getList("name")).contains("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createDuplicateLine() {
        LineRequest line = LineRequest.of("신분당선", "red", 1L, 2L, 10);
        createLine(line);

        LineRequest duplicateLine = LineRequest.of("신분당선", "green", 1L, 2L, 10);
        ExtractableResponse<Response> response = createLine(duplicateLine);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선 색상으로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선 색상으로 지하철 노선을 생성한다.")
    @Test
    void createDuplicateLine2() {
        LineRequest line = LineRequest.of("신분당선", "red", 1L, 2L, 10);
        createLine(line);

        LineRequest duplicateLine = LineRequest.of("분당선", "red", 1L, 2L, 10);
        ExtractableResponse<Response> response = createLine(duplicateLine);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        createLine(LineRequest.of("신분당선", "red", 1L, 2L, 10));
        createLine(LineRequest.of("분당선", "yellow", 1L, 2L, 10));

        ExtractableResponse<Response> response = showAllLines();

        JsonPath jsonPath = response.body().jsonPath();
        Assertions.assertThat(jsonPath.getList("name")).containsExactlyInAnyOrder("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        LineRequest line = LineRequest.of("신분당선", "red", 1L, 2L, 10);
        ExtractableResponse<Response> response = createLine(line);
        long id = response.jsonPath().getLong("id");

        ExtractableResponse<Response> showLineResponse = showLine(id);

        JsonPath jsonPath = showLineResponse.body().jsonPath();
        assertAll(
                () -> assertThat(jsonPath.getLong("id")).isEqualTo(id),
                () -> assertThat(jsonPath.getString("name")).isEqualTo("신분당선"),
                () -> assertThat(jsonPath.getString("color")).isEqualTo("red")
        );
    }

    /**
     * When 존재하지 않는 지하철 노선을 조회하면
     * Then 지하철 노선이 조회되지 않는다.
     */
    @DisplayName("존재하지 않는 지하철 노선을 조회하면 예외가 발생한다.")
    @Test
    void showNotExistLine() {
        ExtractableResponse<Response> response = showLine(0L);

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        LineRequest line = LineRequest.of("신분당선", "red", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = createLine(line);
        long id = createResponse.jsonPath().getLong("id");

        LineUpdateRequest updateLine = LineUpdateRequest.of("new분당선", "red");
        ExtractableResponse<Response> updateResponse = updateLine(id, updateLine);
        ExtractableResponse<Response> showResponse = showLine(id);

        JsonPath jsonPath = showResponse.body().jsonPath();
        assertAll(
                () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getString("name")).isEqualTo("new분당선"),
                () -> assertThat(jsonPath.getString("color")).isEqualTo("red")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        LineRequest line = LineRequest.of("신분당선", "red", 1L, 2L, 10);
        ExtractableResponse<Response> createLineResponse = createLine(line);
        long id = createLineResponse.jsonPath().getLong("id");

        ExtractableResponse<Response> deleteResponse = deleteLine(id);

        JsonPath jsonPath = showAllLines().body().jsonPath();
        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(jsonPath.getList("id")).doesNotContain(id),
                () -> assertThat(jsonPath.getList("name")).doesNotContain("신분당선")
        );
    }

    private ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> showAllLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> showLine(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(Long id, LineUpdateRequest lineRequest) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }
}
