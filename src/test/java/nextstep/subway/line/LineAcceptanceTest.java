package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String path = "/lines";

    private LineRequest request1;
    private LineRequest request2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        request1 = new LineRequest("신분당선", "bg-red-600");
        request2 = new LineRequest("2호선", "bg-green-600");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성(request1, path);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성(request1, path);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);
        지하철_노선_등록되어_있음(request2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회(path);

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);

        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        String location = 지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회(location);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 조회 할 때 없는 노선을 조회하는 경우 조회가 실패한다.")
    @Test
    void getLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_조회_요청
        String notExistLinePath = "/lines/2";
        ExtractableResponse<Response> response = 지하철_노선_조회(notExistLinePath);

        // then
        // 지하철_노선_응답됨
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        String location = 지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(location);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(response);
    }

    @DisplayName("없는 지하철 노선을 수정 할 때 조회가 실패하고 수정되지 않는다.")
    @Test
    void updateLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_수정_요청
        String notExistsLinePath = "/lines/2";
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(notExistsLinePath);

        // then
        // 지하철_노선_수정되지 않음
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        String location = 지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거(location);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    @DisplayName("없는 지하철 노선을 삭제하려고 하면 조회가 실패하고 삭제되지 않는다.")
    @Test
    void deleteLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_제거_요청
        String notExistsLinePath = "/lines/2";
        ExtractableResponse<Response> response = 지하철_노선_제거(notExistsLinePath);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_조회_실패됨(response);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("구분당선");
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String location) {
        LineRequest modifyRequest = new LineRequest("구분당선", "bg-red-600");
        return 지하철_노선_수정(modifyRequest, location);
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response) {
        List<LineResponse> lines = response.body().jsonPath().getList(".", LineResponse.class);

        List<String> lineNames = lines.stream()
            .map(LineResponse::getName)
            .collect(Collectors.toList());

        assertThat(lineNames).containsExactly(request1.getName(), request2.getName());
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        List<LineResponse> lines = response.body().jsonPath().getList(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lines).isNotNull();
        assertThat(lines).hasSize(2);
    }

    private void 지하철_노선_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    private String 지하철_노선_등록되어_있음(LineRequest request) {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성(request, path);
        return createResponse.header("Location");
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    private ExtractableResponse<Response> 지하철_노선_생성(LineRequest params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정(LineRequest params, String path) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }
}
