package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    LineRequest request;

    @BeforeEach
    void setRequest() {
        request = new LineRequest("2호선", "green");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        // then
        지하철노선_응답됨(response, HttpStatus.CREATED);
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
        지하철노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        // then
        지하철노선_응답됨(response, HttpStatus.BAD_REQUEST);
    }

    private void 지하철노선_응답됨(ExtractableResponse<Response> response, HttpStatus badRequest) {
        Assertions.assertThat(response.statusCode()).isEqualTo(badRequest.value());
    }

    @DisplayName("지하철 노선 전체 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        지하철노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회_요청();

        // then
        지하철노선_응답됨(response, HttpStatus.OK);
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
        return response;
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청(request);
        String url = createResponse.header("Location");

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(url);

        // then
        지하철노선_응답됨(getResponse, HttpStatus.OK);
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
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청(request);
        String url = createResponse.header("Location");

        // when
        LineRequest updateRequest = new LineRequest("3호선", "black");
        ExtractableResponse<Response> response = 지하철노선_수정_요청(url, updateRequest);

        // then
        지하철노선_응답됨(response, HttpStatus.OK);
        지하철노선_수정_검증됨(url, updateRequest);
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(String url, LineRequest updateRequest) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(url)
                .then().log().all().extract();
        return response;
    }

    private void 지하철노선_수정_검증됨(String url, LineRequest updateRequest) {
        ExtractableResponse<Response> response = 지하철노선_조회_요청(url);
        LineResponse lineResponse = response.body().as(LineResponse.class);
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(updateRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(updateRequest.getColor())
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청(request);
        String url = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = 지하철노선_제거_요청(url);

        // then
        지하철노선_응답됨(response, HttpStatus.NO_CONTENT);
        지하철노선_삭제_검증됨(url);
    }

    private ExtractableResponse<Response> 지하철노선_제거_요청(String url) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete(url)
                .then().log().all().extract();
        return response;
    }

    private void 지하철노선_삭제_검증됨(String url) {
        ExtractableResponse<Response> response = 지하철노선_조회_요청(url);
        지하철노선_응답됨(response, HttpStatus.BAD_REQUEST);
    }
}
