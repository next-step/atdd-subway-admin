package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
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
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        // when
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(
                "2호선",
                "green",
                1L,
                2L,
                10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then
        // 지하철_노선_생성됨
        assertThat(response.body().jsonPath().get("name").equals("2호선"));
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        지하철_노선_등록되어_있음(
                "2호선",
                "green",
                1L,
                2L,
                10);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(
                "2호선",
                "green",
                1L,
                2L,
                10);

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
        지하철역_생성("신도림역");
        지하철역_생성("영등포역");
        지하철_노선_등록되어_있음(
                "1호선",
                "blue",
                3L,
                4L,
                10);
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(
                "2호선",
                "green",
                1L,
                2L,
                10);
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
        지하철_노선_등록되어_있음(
                "2호선",
                "green",
                1L,
                2L,
                10);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(1);
        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("name").equals("2호선"));
        assertThat(response.body().jsonPath().get("color").equals("green"));
        assertThat(response.body().jsonPath().getList("stations")).isNotEmpty();
        assertThat(response.body().jsonPath().getList("stations", Station.class).stream().map(Station::getId).collect(Collectors.toList())).containsExactly(1L, 2L);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        지하철_노선_등록되어_있음(
                "1호선",
                "blue",
                1L,
                2L,
                10);
        // when
        // 지하철_노선_수정_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "paleblue");
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "1");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(params, pathParams);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("name").equals("1호선"));
        assertThat(response.body().jsonPath().get("color").equals("paleblue"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철역_생성("강남역");
        지하철역_생성("역삼역");
        지하철_노선_등록되어_있음(
                "1호선",
                "blue",
                1L,
                2L,
                10);
        // when
        // 지하철_노선_제거_요청
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "1");
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(pathParams);
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

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name,
                                                         String color,
                                                         Long upStationId,
                                                         Long downStationId,
                                                         Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> params, Map<String, String> pathParams) {
        // 지하철_노선_수정_요청
        return RestAssured
                .given().log().all()
                .pathParams(pathParams)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(Map<String, String> pathParams) {
        // 지하철_노선_삭제_요청
        return RestAssured
                .given().log().all()
                .pathParams(pathParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철역_생성(String name){
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
}
