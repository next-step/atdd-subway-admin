package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        LineRequest request = new LineRequest("2호선", "green");
        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        // then
        지하철노선_생성됨(response, HttpStatus.CREATED);
    }

    private void 지하철노선_생성됨(ExtractableResponse<Response> response, HttpStatus created) {
        Assertions.assertThat(response.statusCode()).isEqualTo(created.value());
    }

    private ExtractableResponse<Response> 지하철노선_생성_요청(LineRequest request) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
        return response;
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequest request = new LineRequest("2호선", "green");
        지하철노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        // then
        지하철노선_생성_실패됨(response, HttpStatus.BAD_REQUEST);
    }

    private void 지하철노선_생성_실패됨(ExtractableResponse<Response> response, HttpStatus badRequest) {
        Assertions.assertThat(response.statusCode()).isEqualTo(badRequest.value());
    }

    @DisplayName("지하철 노선 전체 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        LineRequest request = new LineRequest("2호선", "green");
        지하철노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회_요청();

        // then
        지하철노선_정상_응답됨(response);
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
        return response;
    }

    private void 지하철노선_정상_응답됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineRequest request = new LineRequest("2호선", "green");
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청(request);
        String url = createResponse.header("Location");

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(url);

        // then
        지하철노선_정상_응답됨(getResponse);
        LineResponse lineResponse = getResponse.body().as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(request.getName());
    }

    private ExtractableResponse<Response> 지하철노선_조회_요청(String url) {
        return RestAssured
                .given().log().all()
                .when().get(url)
                .then().log().all().extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }
}
