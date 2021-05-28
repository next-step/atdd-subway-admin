package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final LineRequest INCHEON_SUBWAY_LINE_1 = new LineRequest("인천 1호선", "#7CA8D5");
    private final LineRequest INCHEON_SUBWAY_LINE_2 = new LineRequest("인천 2호선", "#ED8B00");
    private final LineRequest AIRPORT_EXPRESS = new LineRequest("공항철도", "#0065B3");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineRequest() {

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLineRequest(INCHEON_SUBWAY_LINE_1);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location").startsWith("/lines")).isTrue();

        assertThat(response.body().jsonPath().getString("name"))
            .isEqualTo(INCHEON_SUBWAY_LINE_1.getName());

        assertThat(response.body().jsonPath().getString("color"))
            .isEqualTo(INCHEON_SUBWAY_LINE_1.getColor());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createdLine(INCHEON_SUBWAY_LINE_1);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLineRequest(INCHEON_SUBWAY_LINE_1);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.header("Location")).isBlank();
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        createdLine(INCHEON_SUBWAY_LINE_1);
        createdLine(AIRPORT_EXPRESS);

        // when
        // 지하철_노선_목록_조회_요청
        // when
        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                       .when().get("/lines")
                       .then().log().all()
                       .extract();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<LineResponse> lines = response.body().jsonPath().getList("$", LineResponse.class);
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines).extracting(LineResponse::getName)
                         .contains(INCHEON_SUBWAY_LINE_1.getName(), AIRPORT_EXPRESS.getName());
        assertThat(lines).extracting(LineResponse::getColor)
                         .contains(INCHEON_SUBWAY_LINE_1.getColor(), AIRPORT_EXPRESS.getColor());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        createdLine(INCHEON_SUBWAY_LINE_1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = getLineRequest(1);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name"))
            .isEqualTo(INCHEON_SUBWAY_LINE_1.getName());
        assertThat(response.body().jsonPath().getString("color"))
            .isEqualTo(INCHEON_SUBWAY_LINE_1.getColor());
    }

    @DisplayName("존재하지 않는 노선 번호로 노선을 조회한다.")
    @Test
    void getLineFail() {
        // given
        // 지하철_노선_등록되어_있음
        createdLine(INCHEON_SUBWAY_LINE_1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = getLineRequest(100);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        createdLine(INCHEON_SUBWAY_LINE_1);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateLineRequest(1, INCHEON_SUBWAY_LINE_2);
        ExtractableResponse<Response> actual = getLineRequest(1);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.body().jsonPath().getString("name"))
            .isEqualTo(INCHEON_SUBWAY_LINE_2.getName());
        assertThat(actual.body().jsonPath().getString("color"))
            .isEqualTo(INCHEON_SUBWAY_LINE_2.getColor());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        createdLine(INCHEON_SUBWAY_LINE_1);

        // when
        // 지하철_노선_제거_요청
        // when
        ExtractableResponse<Response> response = deleteLineRequest(1);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLineRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                          .body(lineRequest)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines")
                          .then().log().all()
                          .extract();
    }

    private void createdLine(LineRequest lineRequest) {
        createLineRequest(lineRequest);
    }

    private ExtractableResponse<Response> getLineRequest(long lineId) {
        return RestAssured.given().log().all()
                          .body(INCHEON_SUBWAY_LINE_1)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().get("/lines/" + lineId)
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> updateLineRequest(long lineId, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                          .body(lineRequest)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().put("/lines/" + lineId)
                          .then().log().all()
                          .extract();
    }

    private ExtractableResponse<Response> deleteLineRequest(long lineId) {
        return RestAssured.given().log().all()
                          .when().delete("/lines/" + lineId)
                          .then().log().all()
                          .extract();
    }
}
