package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "name");
        params.put("color", "color");

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_생성됨
        final LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(lineResponse.getColor()).isEqualTo("color");
            assertThat(lineResponse.getName()).isEqualTo("name");
        });
    }

    @ParameterizedTest(name = "노선의 이름이 \"{0}\" 일 경우 지하철 노선을 생성하지 못하고 예외 메시지가 발생한다.")
    @MethodSource("blankStrings")
    void createLineWithEmptyName(String name) {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "color");

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("노선의 이름이 빈값일 수 없습니다.");
    }

    @ParameterizedTest(name = "노선의 색상값이 \"{0}\" 일 경우 지하철 노선을 생성하지 못하고 예외 메시지가 발생한다.")
    @MethodSource("blankStrings")
    void createLineWithEmptyColor(String color) {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "name");
        params.put("color", color);

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("노선의 색상값이 빈값일 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 경우 에러가 발생한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        final Map<String, String> params = new HashMap<>();
        params.put("name", "name");
        params.put("color", "color");

        final ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> createResponse2 = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(createResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(createResponse2.jsonPath().getString("message")).isEqualTo("노선의 이름이 중복되었습니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_목록_조회_요청

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_조회_요청

        // then
        // 지하철_노선_응답됨
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

    static Stream<String> blankStrings() {
        return Stream.of("", "   ", null);
    }
}
