package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선");

        // then
        // 지하철_노선_생성됨
        응답_상태코드_확인(response, HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body()).isNotNull();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선");

        // then
        // 지하철_노선_생성_실패됨
        응답_상태코드_확인(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음("bg-red-600", "신분당선");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음("bg-green-600", "2호선");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines");

        // then
        // 지하철_노선_목록_응답됨
        응답_상태코드_확인(response, HttpStatus.OK);
        // 지하철_노선_목록_포함됨
        List<Long> expectedIdList = expectedIdList(createResponse1, createResponse2);
        List<Long> responseIdList = response.jsonPath().getList(".", LineResponse.class).stream()
              .map(LineResponse::getId)
              .collect(Collectors.toList());
        assertThat(responseIdList).containsAll(expectedIdList);
    }

    @DisplayName("비어있는 지하철 노선 목록을 조회한다.")
    @Test
    void showLines2() {
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines");

        // then
        // 지하철_노선_목록_응답됨
        응답_상태코드_확인(response, HttpStatus.OK);
        // 지하철_노선_목록_포함됨
        assertThat(response.jsonPath().getList(".", LineResponse.class)).isEmpty();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        // 지하철_노선_조회_요청
        List<Long> lineIds = expectedIdList(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines/" + lineIds.get(0));

        // then
        // 지하철_노선_응답됨
        응답_상태코드_확인(response, HttpStatus.OK);
        assertThat(response.body().as(LineResponse.class).getId()).isEqualTo(lineIds.get(0));
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine2() {
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines/0");

        // then
        // 지하철_노선_응답됨
        응답_상태코드_확인(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("bg-blue-600", "신분당선");

        // when
        // 지하철_노선_수정_요청
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");
        List<Long> createLineIds = expectedIdList(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("/lines/" + createLineIds.get(0), params);

        // then
        // 지하철_노선_수정됨
        응답_상태코드_확인(response, HttpStatus.OK);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateLine2() {
        // when
        // 지하철_노선_수정_요청
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("/lines/0", params);

        // then
        // 지하철_노선_수정됨
        응답_상태코드_확인(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        // 지하철_노선_제거_요청
        List<Long> lineIds = expectedIdList(createResponse);
        ExtractableResponse<Response> response = 지하철_노선_제거_요청("/lines/" + lineIds.get(0));

        // then
        // 지하철_노선_삭제됨
        응답_상태코드_확인(response, HttpStatus.OK);
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteLine2() {
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청("/lines/0");

        // then
        // 지하철_노선_삭제됨
        응답_상태코드_확인(response, HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String color, String name) {
        return 지하철_노선_생성_요청(color, name);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String path) {
        return RestAssured.given().log().all()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when().get(path)
              .then().log().all()
              .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

        return RestAssured.given().log().all()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(params)
              .when().post("/lines")
              .then().log().all()
              .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String path, Map<String, String> params) {
        return RestAssured.given().log().all()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(params)
              .when().put(path)
              .then().log().all()
              .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(String path) {
        return RestAssured.given().log().all()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when().delete(path)
              .then().log().all()
              .extract();
    }

    private void 응답_상태코드_확인(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private List<Long> expectedIdList(ExtractableResponse<Response>... createResponses) {
        return Arrays.stream(createResponses)
              .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
              .collect(Collectors.toList());
    }
}
