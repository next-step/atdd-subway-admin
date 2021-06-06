package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String URI_PATH = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createTestLine1();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createTestLine1();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createTestLine1();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = createTestLine1();
        ExtractableResponse<Response> createResponse2 = createTestLine2();

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = showLines();

        // then
        assertAll(
            // 지하철_노선_목록_응답됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),

            // 지하철_노선_목록_포함됨
            () -> {
                List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                        .map(this::getLocationId)
                        .collect(Collectors.toList());
                List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                        .map(it -> it.getId())
                        .collect(Collectors.toList());

                assertThat(resultLineIds).containsAll(expectedLineIds);
            }
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createTestLine1();

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = showLines(getLocationId(createResponse));

        // then
        assertAll(
            // 지하철_노선_응답됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),

            // 지하철_노선_포함됨
            () -> {
                Long expected = response.as(LineResponse.class).getId();
                assertThat(getLocationId(createResponse)).isEqualTo(expected);
            }
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createTestLine1();

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateLine(getLocationId(createResponse), getTargetLine("3호선", "yellow lighten-1"));

        // then
        // 지하철_노선_수정됨
        assertAll(
            // 지하철_노선_응답됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),

            // 지하철_노선_포함됨
            () -> {
                LineResponse expected = response.jsonPath().getObject(".", LineResponse.class);
                assertThat(expected.getId()).isEqualTo(getLocationId(createResponse));
                assertThat(expected.getName()).isEqualTo("3호선");
                assertThat(expected.getColor()).isEqualTo("yellow lighten-1");
            }
        );
    }

    @DisplayName("지하철 노선을 수정할때, 기존에 존재하는 노선 이름으로는 변경할수 없다.")
    @Test
    void updateLine2() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = createTestLine1();
        ExtractableResponse<Response> createResponse2 = createTestLine2();

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateLine(getLocationId(createResponse1), getTargetLine("2호선", "green lighten-2"));

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createTestLine1();

        // when
        // 지하철_노선_제거_요청
        long deletedId = getLocationId(createResponse);
        ExtractableResponse<Response> deletedResponse = deleteLine(deletedId);

        // then
        // 지하철_노선_삭제됨
        assertAll(
            // 지하철_노선삭제
            () -> assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),

            // 지하철_노선_찾지못함
            () -> {
                ExtractableResponse<Response> response = showLines(deletedId);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            }
        );
    }

    private Map<String, String> getTargetLine(String name, String color) {
        Map<String, String> params = new HashMap<String, String>(){
            {
                put("name", name);
                put("color", color);
            }
        };
        return Collections.unmodifiableMap(params);
    }

    private ExtractableResponse<Response> createTestLine1() {
        return createLine(getTargetLine("1호선", "blue lighten-1"));
    }

    private ExtractableResponse<Response> createTestLine2() {
        return createLine(getTargetLine("2호선", "green lighten-1"));
    }

    private ExtractableResponse<Response> createLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post(URI_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> updateLine(final Long id, final Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .body(params)
                .when()
                .put(URI_PATH + "/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(final Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when()
                .delete(URI_PATH + "/{id}")
                .then().log().all()
                .extract();
    }

    private long getLocationId(ExtractableResponse<Response> response) {
        String result = response.header("Location").split("/")[2];

        return Long.parseLong(result);
    }

    private ExtractableResponse<Response> showLines() {
        return this.showLines(0L);
    }

    private ExtractableResponse<Response> showLines(final Long id) {
        String path = (id > 0) ? URI_PATH + "/" + id : URI_PATH;

        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }
}
