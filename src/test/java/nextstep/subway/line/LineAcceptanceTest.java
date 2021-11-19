package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 생성")
    @Nested
    class CreateLineTest {

        @DisplayName("지하철 노선을 생성한다.")
        @Test
        void createLine() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("박달-강남선", "blue");

            // then
            지하철_노선이_생성된다(response);
        }

        @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
        @Test
        void givenDuplicateLineNameThenFail() {
            // given
            지하철_노선_등록되어_있음("박달-강남선", "blue");

            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("박달-강남선", "blue");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        @DisplayName("공백의 노선명으로 지하철 노선을 생성한다.")
        @Test
        void givenEmptyNameThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("", "blue");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        @DisplayName("공백의 노선색상으로 지하철 노선을 생성한다.")
        @Test
        void givenEmptyColorThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("황금노선현", "");

            // then
            지하철_노선_생성이_실패한다(response);
        }
    }

    @DisplayName("지하철 노선 목록 조회")
    @Nested
    class GetLinesTest {
        @DisplayName("지하철 노선 목록을 조회한다.")
        @Test
        void getLines() {
            // given
            ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음("박달-강남선", "blue");
            ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음("광명-구디선", "green");

            // when
            ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

            // then
            지하철_노선_목록이_응답된다(response);
            지하철_노선_목록이_포함된다(createResponse1, createResponse2, response);
        }
    }

    @DisplayName("지하철 노선 조회")
    @Nested
    class GetLineTest {
        @DisplayName("지하철 노선을 조회한다.")
        @Test
        void getLine() {
            // given
            ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("박달-강남선", "blue");

            // when
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

            // then
            지하철_노선을_응답한다(response);
        }

        @DisplayName("등록되지 않은 지하철 노선을 조회한다.")
        @Test
        void givenHasNotIdThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

            // then
            지하철_노선_응답이_실패한다(response);
        }
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

    private void 지하철_노선이_생성된다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성이_실패한다(ExtractableResponse<Response> response) {
        assertIsBadRequest(response);
    }

    private void 지하철_노선_응답이_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void 지하철_노선_목록이_응답된다(ExtractableResponse<Response> response) {
        assertIsOk(response);
    }

    private void 지하철_노선을_응답한다(ExtractableResponse<Response> response) {
        assertIsOk(response);
    }

    private void 지하철_노선_목록이_포함된다(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", Line.class).stream()
                .map(Line::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        return 지하철_노선_생성_요청(name, color);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return get("/lines");
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        return get(response.header("Location"));
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return get("/lines/" + id);
    }

    private ExtractableResponse<Response> get(String url) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(url)
                .then().log().all().extract();
    }

    private void assertIsBadRequest(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void assertIsOk(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
