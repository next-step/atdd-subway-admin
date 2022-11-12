package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseInitializer;
import nextstep.subway.line.dto.LineUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineAcceptanceMethods.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseInitializer.afterPropertiesSet();
        }

        databaseInitializer.initialize();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        지하철_노선_생성("신분당선", "red", "신사역", "광교역", 10);

        JsonPath jsonPath = 지하철_노선_목록_조회().body().jsonPath();
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
        지하철_노선_생성("신분당선", "red", "신사역", "광교역", 10);

        ExtractableResponse<Response> response =
                지하철_노선_생성("신분당선", "green", "강남역", "판교역", 10);

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
        지하철_노선_생성("신분당선", "red", "신사역", "광교역", 10);

        ExtractableResponse<Response> response =
                지하철_노선_생성("분당선", "red", "서현역", "수원역", 10);


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
        지하철_노선_생성("신분당선", "red", "신사역", "광교역", 10);
        지하철_노선_생성("분당선", "yellow", "청량리역", "인천역", 10);

        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

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
        ExtractableResponse<Response> response =
                지하철_노선_생성("신분당선", "red", "신사역", "광교역", 10);
        long id = response.jsonPath().getLong("id");

        ExtractableResponse<Response> showLineResponse = 지하철_노선_조회(id);

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
        ExtractableResponse<Response> response = 지하철_노선_조회(0L);

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
        ExtractableResponse<Response> createResponse =
                지하철_노선_생성("신분당선", "red", "신사역", "광교역", 10);
        long id = createResponse.jsonPath().getLong("id");

        LineUpdateRequest updateLine = LineUpdateRequest.of("new분당선", "red");
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정(id, updateLine);
        ExtractableResponse<Response> showResponse = 지하철_노선_조회(id);

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
        ExtractableResponse<Response> createLineResponse =
                지하철_노선_생성("신분당선", "red", "신사역", "광교역", 10);
        long id = createLineResponse.jsonPath().getLong("id");

        ExtractableResponse<Response> deleteResponse = 지하철_노선_삭제(id);

        JsonPath jsonPath = 지하철_노선_목록_조회().body().jsonPath();
        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(jsonPath.getList("id")).doesNotContain(id),
                () -> assertThat(jsonPath.getList("name")).doesNotContain("신분당선")
        );
    }
}
