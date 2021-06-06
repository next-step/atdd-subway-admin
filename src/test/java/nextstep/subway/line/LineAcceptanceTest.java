package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.Line.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    enum Line {
        FIRST(1L, "1호선", "bg-blue-600"),
        SECOND(2L, "2호선", "bg-green-600"),
        THIRD(3L, "2호선", "bg-orange-600");

        private final Long id;
        private final String name;
        private final String color;

        Line(final Long id, final String name, final String color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void given_NoExisingLine_when_CreateLine_then_ReturnLine() {
        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(FIRST.name, FIRST.color);

        // then
        // 지하철_노선_생성됨
        final JsonPath jsonPath = response.jsonPath();
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank(),
            () -> assertThat(jsonPath.getString("id")).isNotNull(),
            () -> assertThat(jsonPath.getString("name")).isNotNull(),
            () -> assertThat(jsonPath.getString("color")).isNotNull()
        );
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(final String name, final String color) {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        // when
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void given_ExistingLine_when_CreateLineAlreadyExisting_then_ReturnBadRequest() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(FIRST.name, FIRST.color);

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(FIRST.name, FIRST.color);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void given_ExistingLine_when_SearchAllLine_then_ReturnLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(FIRST.name, FIRST.color);
        지하철_노선_등록되어_있음(SECOND.name, SECOND.color);

        // when
        // 지하철_노선_목록_조회_요청
        final ExtractableResponse<Response> response = 지하철_노션_조회_요청("/lines");

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        final List<LineResponse> lineResponses = lineResponses(response);
        final LineResponse firstLineResponse = lineResponses.get(0);
        final LineResponse secondLineResponse = lineResponses.get(1);

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(lineResponses.size()).isEqualTo(2),
            () -> assertThat(firstLineResponse.getName()).isEqualTo(FIRST.name),
            () -> assertThat(firstLineResponse.getColor()).isEqualTo(FIRST.color),
            () -> assertThat(secondLineResponse.getName()).isEqualTo(SECOND.name),
            () -> assertThat(secondLineResponse.getColor()).isEqualTo(SECOND.color)
        );
    }

    private ExtractableResponse<Response> 지하철_노션_조회_요청(final String path) {
        return RestAssured.given().log().all()
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    private List<LineResponse> lineResponses(final ExtractableResponse<Response> response) {
        final JsonPath jsonPath = response.jsonPath();

        return jsonPath.getList(".", LineResponse.class);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void given_ExistingLine_when_SearchLine_returnLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(FIRST.name, FIRST.color);

        // when
        // 지하철_노선_조회_요청
        final ExtractableResponse<Response> response = 지하철_노션_조회_요청("/lines/" + FIRST.id);

        // then
        // 지하철_노선_응답됨
        final JsonPath jsonPath = response.jsonPath();
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(jsonPath.getLong("id")).isEqualTo(FIRST.id),
            () -> assertThat(jsonPath.getString("name")).isEqualTo(FIRST.name),
            () -> assertThat(jsonPath.getString("color")).isEqualTo(FIRST.color)
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(FIRST.name, FIRST.color);

        final Map<String, String> params = new HashMap<>();
        params.put("name", THIRD.name);
        params.put("color", THIRD.color);

        // when
        // 지하철_노선_수정_요청
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청("/lines/" + FIRST.id, params);

        // then
        // 지하철_노선_수정됨
        final JsonPath jsonPath = response.jsonPath();
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(jsonPath.getLong("id")).isEqualTo(FIRST.id),
            () -> assertThat(jsonPath.getString("name")).isEqualTo(THIRD.name),
            () -> assertThat(jsonPath.getString("color")).isEqualTo(THIRD.color)
        );
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(final String path, final Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(FIRST.name, FIRST.color);

        // when
        // 지하철_노선_제거_요청
        final ExtractableResponse<Response> response = 지하철_노선_제거_요청("/lines/" + FIRST.id);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(final String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }
}
