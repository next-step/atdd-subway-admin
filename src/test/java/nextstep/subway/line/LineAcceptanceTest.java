package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.Assertions;
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
        // when
        ExtractableResponse<Response> response = 노선_생성(LineRequest.of("1호선","blue",1L,2L,10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineNames(노선_전체_조회())).containsAnyOf("1호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        노선_생성(LineRequest.of("1호선","blue",1L,2L,10));
        노선_생성(LineRequest.of("2호선","green",3L,4L,15));

        //when
        List<String> lineNames = lineNames(노선_전체_조회());

        //then
        Assertions.assertAll(
                () -> assertThat(lineNames.size()).isEqualTo(2),
                () -> assertThat(lineNames).contains("1호선", "2호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        ExtractableResponse<Response> response = 노선_생성(LineRequest.of("1호선", "blue", 1L, 2L, 10));

        //when
        String lineName = lineName(노선_단건_조회(response.jsonPath().getLong("id")));

        //then
        assertThat(lineName).isEqualTo("1호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updataLine() {
        //given
        ExtractableResponse<Response> created = 노선_생성(LineRequest.of("1호선", "blue", 1L, 2L, 10));

        //when
        LineRequest newLine = LineRequest.of("신_1호선", "skyblue", 1L, 2L, 10);
        ExtractableResponse<Response> response = 노선_수정(newLine, created.jsonPath().getLong("id"));

        //then
        assertThat(lineName(response)).isEqualTo("신_1호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        ExtractableResponse<Response> created = 노선_생성(LineRequest.of("1호선", "blue", 1L, 2L, 10));

        //when
        ExtractableResponse<Response> response = 노선_삭제(created.jsonPath().getLong("id"));

        //then
        ExtractableResponse<Response> lines = 노선_전체_조회();
        assertThat(lineNames(lines)).doesNotContain("1호선");
    }

    private ExtractableResponse<Response> 노선_생성(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }


    private ExtractableResponse<Response> 노선_생성2(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_수정(LineRequest newLine, Long lineId) {
        return RestAssured.given().log().all()
                .body(newLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_전체_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_단건_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_삭제(long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private List<String> lineNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    private String lineName(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }
}
