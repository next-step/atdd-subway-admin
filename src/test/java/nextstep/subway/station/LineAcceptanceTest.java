package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
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

@DisplayName("지하철 노선 관련 기능")
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
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철역_노선_이름_목록조회();
        assertThat(lineNames).containsAnyOf("4호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("생성한 모든 노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        // given
        지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역");
        지하철_노선_생성("1호선", "파란색", 10, "소요산역", "인천역");

        // when
        List<String> lineNames = 지하철역_노선_이름_목록조회();

        // then
        Assertions.assertAll(
                () -> assertThat(lineNames).hasSize(2),
                () -> assertThat(lineNames).containsExactly("4호선", "1호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("생성한 노선을 조회한다.")
    @Test
    void findLineById() {
        // given
        long lineId = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역").jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_노선_정보_조회(lineId);
        LineResponse lineResponse = convertLineResponse(response);

        // then
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("4호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("하늘색")
        );

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선 이름과 색갈을 수정한다.")
    @Test
    void modifyLineById() {
        // given
        long lineId = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역").jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> lineModifyResponse = 지하철역_노선_수정(lineId, "1호선", "파란색");

        // then
        assertThat(lineModifyResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> response = 지하철역_노선_정보_조회(lineId);
        LineResponse lineResponse = convertLineResponse(response);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("1호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("파란색")
        );


    }

    private LineResponse convertLineResponse(ExtractableResponse<Response> response) {
        return response.body().as(LineResponse.class);
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String lineName, String color, int distance,
                                                    String upStationName, String downStationName) {
        Long upStationId = 지하철역_생성(upStationName).jsonPath().getLong("id");
        Long downStationId = 지하철역_생성(downStationName).jsonPath().getLong("id");

        LineRequest lineRequest = new LineRequest(lineName, color, distance, upStationId, downStationId);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private List<String> 지하철역_노선_이름_목록조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> 지하철역_노선_정보_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철역_노선_수정(Long lineId, String lineName, String color) {
        LineRequest lineRequest = new LineRequest();
        lineRequest.setName(lineName);
        lineRequest.setColor(color);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철역_생성(String stationName) {
        StationRequest stationRequest = new StationRequest(stationName);

        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
}
