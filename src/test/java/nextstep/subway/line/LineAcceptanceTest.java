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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String API_PATH = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "초록노선");
        params.put("color", "초록");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        // then
        assertAll(() -> {
            지하철_노선_생성됨(response);
            지하철_노선_생성_결과_검증(response, params);
        });
    }

    @ParameterizedTest(name = "노선의 이름이 \"{0}\" 일 경우 지하철 노선을 생성하지 못하고 예외 메시지가 발생한다.")
    @MethodSource("blankStrings")
    void createLineWithEmptyName(String name) {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "색상");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        // then
        지하철_노선_생성_실패됨(response, "노선의 이름이 빈값일 수 없습니다.");
    }


    @ParameterizedTest(name = "노선의 색상값이 \"{0}\" 일 경우 지하철 노선을 생성하지 못하고 예외 메시지가 발생한다.")
    @MethodSource("blankStrings")
    void createLineWithEmptyColor(String color) {
        // given
        final Map<String, String> params = new HashMap<>();
        params.put("name", "name");
        params.put("color", color);
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        // then
        지하철_노선_생성_실패됨(response, "노선의 색상값이 빈값일 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 경우 에러가 발생한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음("name", "color");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_등록되어_있음("name", "color");
        // then
        지하철_노선_생성_실패됨(response, "노선의 이름이 중복되었습니다.");
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        // given
        final LineResponse 노란노선 = 지하철_노선_등록되어_있음("노란노선", "노랑색").as(LineResponse.class);
        final LineResponse 초록노선 = 지하철_노선_등록되어_있음("초록노선", "초록노선").as(LineResponse.class);
        // when
        final ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(노란노선, 초록노선));
        지하철_노선_목록의_항목_검증(response, Arrays.asList(노란노선, 초록노선));
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


    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(API_PATH)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<LineResponse> lines) {
        final List<LineResponse> lineListResponse = response.jsonPath().getList(".", LineResponse.class);
        assertThat(lineListResponse).containsAll(lines);
    }

    private void 지하철_노선_목록의_항목_검증(ExtractableResponse<Response> response, List<LineResponse> lines) {

        List<String> names = lines.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());

        List<String> colors = lines.stream()
                .map(LineResponse::getColor)
                .collect(Collectors.toList());

        assertThat(response.jsonPath().getList("name", String.class)).containsAll(names);
        assertThat(response.jsonPath().getList("color", String.class)).containsAll(colors);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(API_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {

        assert name != null;
        assert color != null;

        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return 지하철_노선_생성_요청(params);
    }

    private void 지하철_노선_생성_결과_검증(ExtractableResponse<Response> response, Map<String, String> params) {
        final LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertAll(() -> {
            assertThat(lineResponse.getColor()).isEqualTo(params.get("color"));
            assertThat(lineResponse.getName()).isEqualTo(params.get("name"));
        });
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response, String errorMessage) {
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.jsonPath().getString("message")).isEqualTo(errorMessage);
        });
    }

    static Stream<String> blankStrings() {
        return Stream.of("", "   ", null);
    }
}
