package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    public static final String LINE_URL = "/lines";
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
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // When
        ExtractableResponse<Response> response = createLineRest(new LineRequest("신분당선","bg-red-600", 1L, 2L,10));

        // Then
        ExtractableResponse<Response> lineNamesResponse =
                RestAssured.given().log().all()
                        .when().get(LINE_URL)
                        .then().log().all()
                        .extract();
        assertThat(lineNamesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineNamesResponse.jsonPath().getList("name", String.class)).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        createLineRest(new LineRequest("신분당선","bg-red-600", 1L, 2L,10));
        createLineRest(new LineRequest("분당선","bg-green-600", 1L, 2L,10));

        // when
        List<String> lineNames = linesNamesList(linesNameGet());

        // then
        assertThat(lineNames.size()).isEqualTo(2);
        assertThat(lineNames).contains("신분당선","분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> response = createLineRest(new LineRequest("신분당선","bg-red-600", 1L, 2L,10));

        // when
        Long id = response.jsonPath().getLong("id");
        ExtractableResponse<Response> lineNameResponse = lineGet(id);

        // then
        assertThat(lineNameResponse.jsonPath().getString("name")).isEqualTo("신분당선");

    }



    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> response = createLineRest(new LineRequest("신분당선","bg-red-600", 1L, 2L,10));

        // when
        Long id = response.jsonPath().getLong("id");
        ExtractableResponse<Response> lineNameResponse = linePut(id, new LineRequest("다른분당선","bg-red-600",1L,2L,10));

        // then
        assertThat(lineNameResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineNameResponse.jsonPath().getString("name")).isEqualTo("다른분당선");

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> response = createLineRest(new LineRequest("신분당선","bg-red-600", 1L, 2L,10));

        // when
        Long id = response.jsonPath().getLong("id");
        deletelineDelete(id);

        // then
        ExtractableResponse<Response> lineNamesResponse =
                RestAssured.given().log().all()
                        .when().get(LINE_URL)
                        .then().log().all()
                        .extract();
        assertThat(lineNamesResponse.jsonPath().getList("name", String.class)).doesNotContain("신분당선");
    }


    private ExtractableResponse<Response> createLineRest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINE_URL)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> linesNameGet() {
        return RestAssured.given().log().all()
                .when().get(LINE_URL)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> lineGet(Long id) {
        return RestAssured.given().log().all()
                .when().get(LINE_URL+"/{id}",id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> linePut(Long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(LINE_URL+"/{id}",id)
                .then().log().all()
                .extract();
    }

    private void deletelineDelete(Long id) {
        RestAssured.given().log().all()
                .when().delete(LINE_URL+"/{id}",id)
                .then().log().all();
    }

    private List<String> linesNamesList(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }
}
