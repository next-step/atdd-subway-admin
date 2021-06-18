package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final long 강남역_ID = 1L;
    private static final long 역삼역_ID = 2L;
    private static final long 영등포역_ID = 3L;
    private static final long 신도림역_ID = 4L;
    private static final long LINE_ID = 1L;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        LineRequest lineRequest = new LineRequest("2호선", "green", 강남역_ID, 역삼역_ID, 10);
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then
        // 지하철_노선_생성됨
        지하철_노선응답__성공(lineRequest, response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        LineRequest lineRequest = new LineRequest("2호선", "green", 강남역_ID, 역삼역_ID, 10);
        지하철_노선_등록되어_있음(lineRequest);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        지하철역_생성("영등포역");
        지하철역_생성("신도림역");
        LineRequest lineRequest_1호선 = new LineRequest("1호선", "blue", 영등포역_ID, 신도림역_ID, 10);
        LineRequest lineRequest_2호선 = new LineRequest("2호선", "green", 강남역_ID, 역삼역_ID, 10);
        지하철_노선_등록되어_있음(lineRequest_1호선);
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(lineRequest_2호선);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        assertThat(response.body().jsonPath().getList("name")).contains("1호선", "2호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        LineRequest lineRequest_2호선 = new LineRequest("2호선", "green", 강남역_ID, 역삼역_ID, 10);
        지하철_노선_등록되어_있음(lineRequest_2호선);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(LINE_ID);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        지하철_노선응답__성공(lineRequest_2호선, response);
        지하철노선응답에_역을_포함한다(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        LineRequest lineRequest_2호선 = new LineRequest("2호선", "green", 강남역_ID, 역삼역_ID, 10);
        지하철_노선_등록되어_있음(lineRequest_2호선);

        // when
        // 지하철_노선_수정_요청
        LineRequest lineRequest_update = new LineRequest("2호선", "lime", 영등포역_ID, 신도림역_ID, 10);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(LINE_ID, lineRequest_update);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        지하철_노선응답__성공(lineRequest_2호선, response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        LineRequest lineRequest_2호선 = new LineRequest("2호선", "green", 강남역_ID, 역삼역_ID, 10);
        지하철_노선_등록되어_있음(lineRequest_2호선);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(LINE_ID);
        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선목록_조회_요청() {
        // 지하철_노선_조회_요청
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(long id) {
        // 지하철_노선_조회_요청
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", request.getName());
        params.put("color", request.getColor());
        params.put("upStationId", request.getUpStationId());
        params.put("downStationId", request.getDownStationId());
        params.put("distance", request.getDistance());
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(long id, LineRequest request) {
        // 지하철_노선_수정_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", request.getName());
        params.put("color", request.getColor());
        return RestAssured
                .given().log().all()
                .pathParam("id", id)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(long id) {
        // 지하철_노선_삭제_요청
        return RestAssured
                .given().log().all()
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철역_생성(String name) {
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", name);
        return RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private void 지하철_노선응답__성공(LineRequest request, ExtractableResponse<Response> response) {
        assertThat(response.body().jsonPath().get("name").equals(request.getName()));
        assertThat(response.body().jsonPath().get("color").equals(request.getColor()));
    }

    private void 지하철노선응답에_역을_포함한다(ExtractableResponse<Response> response) {
        assertThat(response.body().jsonPath().getList("stations")).isNotEmpty();
        assertThat(response.body().jsonPath().getList("stations", Station.class).stream()
                .map(Station::getId)
                .collect(Collectors.toList())).containsExactly(강남역_ID, 역삼역_ID);
    }
}
