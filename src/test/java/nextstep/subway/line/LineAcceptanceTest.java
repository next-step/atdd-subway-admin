package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.exception.ErrorResponse;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String NAME_DUPLICATE_EXCEPTION = "이미 존재하는 이름입니다. : ";
    private static final String LINE_NOT_FOUND_EXCEPTION = " : 존재하지 않는 라인입니다.";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLine("2호선", "green");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotNull();
        assertThat(response.header("Location")).contains("/lines/");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        String name = "2호선";
        createLine(name, "green");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLine(name, "green");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getError(response).getMessage()).isEqualTo(NAME_DUPLICATE_EXCEPTION + name);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        Long firstLine = getIdWithResponse(createLine("1호선", "navy"));
        Long secondLine = getIdWithResponse(createLine("2호선", "green"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = getLine("");
        List<Long> resultIds = getIdsWithResponse(response);

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        assertThat(resultIds).containsExactly(firstLine, secondLine);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        String name = "2호선";
        Long id = getIdWithResponse(createLine(name, "green"));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = getLine("/" + id);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse(response).getName()).isEqualTo(name);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine2() {
        // when
        // 지하철_노선_조회_요청
        Long id = 1l;
        ExtractableResponse<Response> response = getLine("/" + id);

        // then
        // 지하철_노선_응답_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getError(response).getMessage()).isEqualTo(id + LINE_NOT_FOUND_EXCEPTION);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdLine = createLine("2호선", "green");

        // when
        // 지하철_노선_수정_요청
        String name = "1호선";
        ExtractableResponse<Response> response = updateLine(name, "navy",
            getIdWithResponse(createdLine));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse(response).getName()).isEqualTo(name);
    }

    @DisplayName("이미 존재하는 이름으로 지하철 노선을 수정한다.")
    @Test
    void updateLine2() {
        // given
        // 지하철_노선_등록되어_있음
        String name = "1호선";
        ExtractableResponse<Response> firstLine = createLine(name, "navy");
        ExtractableResponse<Response> secondLine = createLine("2호선", "green");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateLine(name, "navy",
            getIdWithResponse(secondLine));

        // then
        // 지하철_노선_수정_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getError(response).getMessage()).isEqualTo(NAME_DUPLICATE_EXCEPTION + name);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdLine = createLine("2호선", "green");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = deleteLine(getIdWithResponse(createdLine));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteLine2() {
        // when
        // 지하철_노선_제거_요청
        Long id = 1L;
        ExtractableResponse<Response> response = deleteLine(id);

        // then
        // 지하철_노선_삭제_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getError(response).getMessage()).isEqualTo(id + LINE_NOT_FOUND_EXCEPTION);
    }

    private ExtractableResponse<Response> createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }


    private ExtractableResponse<Response> getLine(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines" + path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> updateLine(String name, String color, Long id) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private List<Long> getIdsWithResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(l -> l.getId())
            .collect(Collectors.toList());
    }

    private Long getIdWithResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

    private LineResponse getResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private ErrorResponse getError(ExtractableResponse<Response> response) {
        return response.response().jsonPath().getObject(".", ErrorResponse.class);
    }
}
