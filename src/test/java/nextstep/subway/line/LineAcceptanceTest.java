package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String RESOURCES = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        ExtractableResponse<Response> response = createLineAsTestCase(request);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(request.getName());
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(request.getColor());
    }

    private ExtractableResponse<Response> createLineAsTestCase(LineRequest request) {
        return RestAssured
            .given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(RESOURCES)
            .then()
            .log().all().extract();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        createLineAsTestCase(request);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLineAsTestCase(request);
        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo("이미 존재하는 Line 입니다");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        createLineAsTestCase(new LineRequest("1호선", "bg-blue-100"));
        // 지하철_노선_등록되어_있음
        createLineAsTestCase(new LineRequest("2호선", "bg-green-200"));
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get(RESOURCES)
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        assertThat(response.body().jsonPath().getList("name")).contains("1호선");
        assertThat(response.body().jsonPath().getList("name")).contains("2호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expected = createLineAsTestCase(new LineRequest("3호선", "bg-orange-100"));
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get(expected.header("LOCATION"))
                .then().log().all().extract();
        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expected = createLineAsTestCase(new LineRequest("3호선", "bg-orange-100"));
        // when
        // 지하철_노선_수정_요청
        LineRequest updateRequest = new LineRequest("3호선", "bg-orange-300");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(expected.header("LOCATION"))
                .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expected = createLineAsTestCase(new LineRequest("3호선", "bg-orange-100"));
        // when
        // 지하철_노선_제거_요청
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete(expected.header("LOCATION"))
                .then().log().all().extract();
        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
