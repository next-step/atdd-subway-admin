package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = requestToCreateLine(params);

        // then
        createLineSuccess(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        lineCreated("신분당선", "red");
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = requestToCreateLine(params);

        // then
        createLineFail(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        long id1 = lineCreated("신분당선", "red");
        long id2 = lineCreated("2호선", "green");
        LocalDateTime now = LocalDateTime.now();
        List<LineResponse> expected = Arrays.asList(new LineResponse(id1, "신분당선", "red", now, now),
            new LineResponse(id2, "2호선", "green", now, now));

        // when
        ExtractableResponse<Response> response = requestToCreateLineList();

        // then
        createLineListSuccess(response);
        lineListContainsExpected(response, expected);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long id = lineCreated("신분당선", "red");
        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "신분당선", "red", now, now);

        // when
        ExtractableResponse<Response> response = reqeustToGetLine(id);

        // then
        getLineSuccess(response, expected);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회하여 실패한다.")
    @Test
    void getLine_throwsExceptionWHenNoExist() {
        // given

        // when
        ExtractableResponse<Response> response = reqeustToGetLine(1L);

        // then
        getLineNotFound(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long id = lineCreated("신분당선", "red");
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");
        LocalDateTime now = LocalDateTime.now();
        LineResponse expected = new LineResponse(id, "2호선", "green", now, now);

        // when
        ExtractableResponse<Response> response = requestToUpdateLine(id, params);

        // then
        updateLineSuccess(response, expected);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정하여 실패한다.")
    @Test
    void updateLine_throwsExceptionWHenNoExist() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "green");

        // when
        ExtractableResponse<Response> response = requestToUpdateLine(1L, params);

        // then
        getLineNotFound(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        long id = lineCreated("신분당선", "red");

        // when
        ExtractableResponse<Response> response = requestToDeleteLine(id);

        // then
        lineDeleteSuccess(response);
    }

    @DisplayName("지하철_노선_생성_요청")
    private ExtractableResponse<Response> requestToCreateLine(Map<String, String> params) {
        // when
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_생성됨")
    private void createLineSuccess(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철_노선_등록되어_있음")
    private long lineCreated(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        ExtractableResponse<Response> response = requestToCreateLine(params);
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    @DisplayName("지하철_노선_생성_실패됨")
    private void createLineFail(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철_노선_목록_조회_요청")
    private ExtractableResponse<Response> requestToCreateLineList() {
        // when
        return RestAssured
            .given().log().all()
            .when().get("/lines")
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_목록_응답됨")
    private void createLineListSuccess(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철_노선_목록_포함됨")
    private void lineListContainsExpected(ExtractableResponse<Response> response, List<LineResponse> expected) {
        Set<LineResponse> lineResponses = new HashSet<>(response.jsonPath().getList(".", LineResponse.class));

        for (LineResponse lineResponse : expected) {
            assertThat(lineResponses.contains(lineResponse)).isTrue();
        }
    }

    @DisplayName("지하철_노선_조회_요청")
    private ExtractableResponse<Response> reqeustToGetLine(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_응답됨")
    private void getLineSuccess(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isEqualTo(expected);
    }

    @DisplayName("지하철_노선_미존재_응답됨")
    private void getLineNotFound(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철_노선_수정_요청")
    private ExtractableResponse<Response> requestToUpdateLine(long id, Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_수정됨")
    private void updateLineSuccess(ExtractableResponse<Response> response, LineResponse expected) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = reqeustToGetLine(expected.getId());
        getLineSuccess(getResponse, expected);
    }

    @DisplayName("지하철_노선_제거_요청")
    private ExtractableResponse<Response> requestToDeleteLine(long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/" + id)
            .then().log().all().extract();
    }

    @DisplayName("지하철_노선_삭제됨")
    private void lineDeleteSuccess(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
