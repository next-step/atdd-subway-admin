package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptance.toStationId;
import static nextstep.subway.station.StationAcceptance.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.db.DataInitializer;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 인수 테스트")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DataInitializer dataInitializer;

    private Long 대림역_id;
    private Long 신대방역_id;
    private Long 강남역_id;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        대림역_id = toStationId(지하철역_생성("대림역"));
        신대방역_id = toStationId(지하철역_생성("신대방역"));
        강남역_id = toStationId(지하철역_생성("강남역"));
    }

    @AfterEach
    void cleanUp() {
        dataInitializer.execute("line");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> saveResponse = 지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L);

        // then
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> getResponse = 지하철_노선_목록_조회();
        List<LineResponse> responses = toList(getResponse);
        LineResponse result = responses.get(0);

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responses).hasSize(1);
        assertThat(result.getName()).isEqualTo("2호선");
    }

    /**
     * given 2개의 지하철 노선을 생성하고
     * when 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L);
        지하철_노선_생성("신분당선", "red", 대림역_id, 강남역_id, 15L);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(toLineNames(response)).hasSize(2);
        assertThat(toLineNames(response)).containsAnyOf("2호선", "신분당선");
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 조회하면
     * Then 생성한 지하촐 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> saveResponse = 지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L);

        // when
        Long 지하철_노선_ID = toLineId(saveResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회(지하철_노선_ID);

        // then
        LineResponse result = toLine(response);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("2호선");
        assertThat(result.getStations()).hasSize(2);
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * Then 지하철 노선 조회 시 변경된 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> saveResponse = 지하철_노선_생성("2호선", "yellow", 대림역_id, 신대방역_id, 10L);

        // when
        Long 지하철_노선_ID = toLineId(saveResponse);
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정(지하철_노선_ID, "8호선", "skyblue");

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = 지하철_노선_조회(지하철_노선_ID);
        assertThat(toLineName(getResponse)).isEqualTo("8호선");
        assertThat(toColorName(getResponse)).isEqualTo("skyblue");
    }

    /**
     * given 지하철 노선을 생성하고 when 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
//        ExtractableResponse<Response> saveResponse = 지하철_노선_생성("신분당선", "black");
//
//        // when
//        Long 지하철_노선_ID = toId(saveResponse);
//        ExtractableResponse<Response> deleteResponse = 지하철_노선_삭제(지하철_노선_ID);
//
//        // then
//        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * 전달받은 지하철역 목록을 저장한다
     *
     * @param name  노선 이름
     * @param color 노선 색상
     */
    private ExtractableResponse<Response> 지하철_노선_생성(String name, String color, Long upStationId,
        Long downStationId, Long distance) {
        LineRequest lineRequest = LineRequest.of(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    /**
     * 지하철 노선을 조회한다
     */
    private ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    /**
     * 지하철 노선 목록을 조회한다
     */
    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    /**
     * 지하철 노선을 수정한다
     */
    private ExtractableResponse<Response> 지하철_노선_수정(Long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    /**
     * 지하철 노선을 삭제한다
     */
    private ExtractableResponse<Response> 지하철_노선_삭제(Long id) {
        return RestAssured.given().log().all()
            .when().delete("/lines/" + id)
            .then().log().all()
            .extract();
    }


    private Long toLineId(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getId();
    }

    private LineResponse toLine(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject("", LineResponse.class);
    }

    private String toLineName(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getName();
    }

    private String toColorName(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getColor();
    }

    private List<String> toLineNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    private List<LineResponse> toList(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("", LineResponse.class);
    }
}
