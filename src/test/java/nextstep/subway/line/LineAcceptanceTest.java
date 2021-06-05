package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("신분당선", "bg-red-600"));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {

        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("신분당선", "bg-red-600"));

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> line1 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("신분당선", "bg-red-600"));
        ExtractableResponse<Response> line2 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("2호선", "bg-green-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 전체_목록_추출();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = 생성된_결과로_ID리스트_추출(Arrays.asList(line1, line2));
        List<Long> resultLineIds = 응답_결과로_ID리스트_추출(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line2 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("신분당선", "bg-red-600"));
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = ID로_지하철_조회(생성_결과중_ID추출(line2));

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getIdToCreatedResponse(line2)).isEqualTo(getIdToResponse(response));
    }

    @DisplayName("지하철 없는 노선을 조회한다.")
    @Test
    void getNoLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line2 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("신분당선", "bg-red-600"));
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = ID로_지하철_조회("/lines/2");

        // then
        // NOT_FOUND 생성
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line2 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("2호선", "bg-green-600"));

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정(생성_결과중_ID추출(line2));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("없는 노선을 수정한다.")
    @Test
    void updateNoLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line2 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("2호선", "bg-green-600"));

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정("/lines/2");

        // then
        // NOT_FOUND 생성
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line2 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("2호선", "bg-green-600"));
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거(생성_결과중_ID추출(line2));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 지하철 노선을 제거한다.")
    @Test
    void deleteNoLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line2 = 파라메터로_지하철_노선_생성(이름_색_파라메터_생성("2호선", "bg-green-600"));
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거("/lines/2");

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거(String path) {
        return RestAssured.given().log().all()
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(이름_색_파라메터_생성("구분당선", "bg-blue-600"))
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> ID로_지하철_조회(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    private String 생성_결과중_ID추출(ExtractableResponse<Response> response) {
        return String.format("/lines/%d", getIdToCreatedResponse(response));
    }

    private Long getIdToResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

    private ExtractableResponse<Response> 파라메터로_지하철_노선_생성(LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private LineRequest 이름_색_파라메터_생성(String name, String color) {
        LineRequest request = new LineRequest(name, color);
        return request;
    }

    private List<Long> 응답_결과로_ID리스트_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(line -> line.getId())
            .collect(Collectors.toList());
    }

    private List<Long> 생성된_결과로_ID리스트_추출(List<ExtractableResponse<Response>> list) {
        return list.stream()
            .map(LineAcceptanceTest::getIdToCreatedResponse)
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> 전체_목록_추출() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private static Long getIdToCreatedResponse(ExtractableResponse<Response> reponse) {
        return Long.parseLong(reponse.header("Location").split("/")[2]);
    }

}
