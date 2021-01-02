package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse stationResponse1;
    private StationResponse stationResponse2;

    @BeforeEach
    void createStations() {
        this.stationResponse1 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("강남역")));
        this.stationResponse2 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("선릉역")));
    }


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("수인분당선", "YELLOW", stationResponse1.getId(), stationResponse2.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청(new LineRequest("수인분당선", "YELLOW", stationResponse1.getId(), stationResponse2.getId(), 5));

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(new LineRequest("수인분당선", "YELLOW", stationResponse1.getId(), stationResponse2.getId(), 10));

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> created1 = 지하철_노선_생성_요청(new LineRequest("수인분당선", "YELLOW", stationResponse1.getId(), stationResponse2.getId(), 5));
        ExtractableResponse<Response> created2 = 지하철_노선_생성_요청(new LineRequest("신분당선", "RED", stationResponse1.getId(), stationResponse2.getId(), 3));
        ExtractableResponse<Response> created3 = 지하철_노선_생성_요청(new LineRequest("2호선", "GREEN", stationResponse1.getId(), stationResponse2.getId(), 2));
        ExtractableResponse<Response> created4 = 지하철_노선_생성_요청(new LineRequest("3호선", "ORANGE", stationResponse1.getId(), stationResponse2.getId(), 1));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        정상_OK_응답됨(response);
        생성된_지하철_노선_목록_응답된_지하철_노선_목록_검증(response, asList(created1, created2, created3, created4), "$");
    }

    @DisplayName("지하철역 포함하여 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> created1 = 지하철_노선_생성_요청(new LineRequest("2호선", "GREEN", stationResponse1.getId(), stationResponse2.getId(), 5));
        ExtractableResponse<Response> created2 = 지하철_노선_생성_요청(new LineRequest("3호선", "ORANGE", stationResponse1.getId(), stationResponse2.getId(), 5));
        Long id = 생성된_지하철_노선_목록(asList(created2)).get(0);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회(id);

        // then
        정상_OK_응답됨(response);
        생성된_지하철_노선_목록_응답된_지하철_노선_검증(response, asList(created1, created2), "$");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> created = 지하철_노선_생성_요청(new LineRequest("분당선", "YELLOW", stationResponse1.getId(), stationResponse2.getId(), 5));
        Long id = 생성된_지하철_노선_목록(asList(created)).get(0);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(id, new LineRequest("수인분당선", "GOLD"));

        // then
        정상_OK_응답됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> created = 지하철_노선_생성_요청(new LineRequest("3호선", "ORANGE", stationResponse1.getId(), stationResponse2.getId(), 5));
        Long id = 생성된_지하철_노선_목록(asList(created)).get(0);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

        // then
        정상_NO_CONTENT_응답됨(response);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 정상_OK_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 정상_NO_CONTENT_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 생성된_지하철_노선_목록_응답된_지하철_노선_목록_검증(ExtractableResponse<Response> response,
                                                      List<ExtractableResponse<Response>> createdResponses,
                                                      String jsonPathOperator) {
        List<Long> expectedLineIds = 생성된_지하철_노선_목록(createdResponses);
        List<Long> resultLineIds = response.jsonPath()
                .getList(jsonPathOperator, LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 생성된_지하철_노선_목록_응답된_지하철_노선_검증(ExtractableResponse<Response> response,
                                                      List<ExtractableResponse<Response>> createdResponses,
                                                      String jsonPathOperator) {
        List<Long> expectedLineIds = 생성된_지하철_노선_목록(createdResponses);
        assertThat(expectedLineIds).contains(response.as(LineResponse.class).getId());
    }

    public static List<Long> 생성된_지하철_노선_목록(List<ExtractableResponse<Response>> createdResponses) {
        return createdResponses.stream()
                .map(r -> r.as(LineResponse.class).getId())
                .collect(toList());
    }
}
