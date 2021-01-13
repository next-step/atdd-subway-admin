package nextstep.subway.line;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.exception.dto.ErrorResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        // then
        지하철_노선_생성됨(response, request);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineDuplicated() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        LineResponse lineResponse = 지하철_노선_등록되어_있음(request);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        // then
        지하철_노선_중복_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest requestNewBoonDangLine = new LineRequest("신분당선", "bg-red-600");
        LineRequest requestLine2 = new LineRequest("2호선", "bg-green-600");
        LineResponse responseNewBoonDangLine = 지하철_노선_등록되어_있음(requestNewBoonDangLine);
        LineResponse responseLine2 = 지하철_노선_등록되어_있음(requestLine2);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, responseNewBoonDangLine, responseLine2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        LineResponse createdLine = 지하철_노선_등록되어_있음(request);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdLine.getId());

        // then
        지하철_노선_응답됨(response, createdLine);
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        LineResponse createdResponse = 지하철_노선_등록되어_있음(request);
        LineRequest modifyRequest = new LineRequest("2호선", "bg-green-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdResponse.getId(), modifyRequest);

        // then
        지하철_노선_수정됨(response, createdResponse.getId(), modifyRequest);
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

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final LineRequest lineRequest) {
        return given().log().all()
            .body(lineRequest)
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return given().log().all()
            .accept(MediaType.ALL_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(final Long id) {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_생성됨(final ExtractableResponse<Response> response, final LineRequest request) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank(),
            () -> assertThat(response.as(LineResponse.class).getName()).isEqualTo(request.getName()),
            () -> assertThat(response.as(LineResponse.class).getColor()).isEqualTo(request.getColor())
        );
    }

    private LineResponse 지하철_노선_등록되어_있음(final LineRequest request) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);
        return response.as(LineResponse.class);
    }


    private void 지하철_노선_목록_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(final ExtractableResponse<Response> response,
        final LineResponse... lineResponses) {
        List<LineResponse> responses = response.body()
            .jsonPath()
            .getList(".", LineResponse.class);

        List<String> createLineNames = Arrays.stream(lineResponses)
            .map(LineResponse::getName)
            .collect(Collectors.toList());

        assertThat(responses.stream().map(LineResponse::getName)
            .collect(Collectors.toList())).containsAll(createLineNames);
    }

    private void 지하철_노선_응답됨(final ExtractableResponse<Response> response, final LineResponse lineResponse) {
        assertAll(
            () -> assertThat(response.as(LineResponse.class).getId()).isEqualTo(lineResponse.getId()),
            () -> assertThat(response.as(LineResponse.class).getName()).isEqualTo(lineResponse.getName()),
            () -> assertThat(response.as(LineResponse.class).getColor()).isEqualTo(lineResponse.getColor())
        );
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(final Long id, final LineRequest modifyRequest) {
        return given().log().all()
            .body(modifyRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .patch("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_수정됨(final ExtractableResponse<Response> response, final Long id,
        final LineRequest modifyRequest) {
        LineResponse modifiedResponse = response.as(LineResponse.class);
        assertAll(
            () -> assertThat(modifiedResponse.getId()).isEqualTo(id),
            () -> assertThat(modifiedResponse.getName()).isEqualTo(modifyRequest.getName()),
            () -> assertThat(modifiedResponse.getColor()).isEqualTo(modifyRequest.getColor())
        );
    }

    private void 지하철_노선_중복_생성_실패됨(final ExtractableResponse<Response> response) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(response.as(ErrorResponse.class).getMessage())
                .isEqualTo("기본 키 또는 유니크 제약조건에 위배됩니다.")
        );
    }

}
