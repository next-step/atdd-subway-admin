package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
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
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        createLine(LineRequest.of("신분당선", "bg-red-600", 1L, 2L, 10));

        ExtractableResponse<Response> showLinesResponse = showLines();

        JsonPath jsonPath = showLinesResponse.body().jsonPath();
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
        LineRequest lineRequest = LineRequest.of("신분당선", "red", 1L, 2L, 10);
        createLine(lineRequest);

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
        LineRequest lineRequest = LineRequest.of("신분당선", "red", 1L, 2L, 10);
        createLine(lineRequest);

        LineRequest duplicateLine = LineRequest.of("분당선", "red", 1L, 2L, 10);
        ExtractableResponse<Response> response = createLine(duplicateLine);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> showLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }
}
