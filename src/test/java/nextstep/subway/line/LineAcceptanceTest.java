package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static nextstep.subway.line.LineAcceptanceTestFactory.*;
import static nextstep.subway.line.LineAcceptanceVerify.*;
import static nextstep.subway.utils.AssertUtils.assertBadRequestAndMessage;
import static nextstep.subway.utils.AssertUtils.assertHttpStatusOk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final Map<String, String> params = getLineCreateParams("초록노선", "초록");
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
        final Map<String, String> params = getLineCreateParams(name, "미지의색상");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        // then
        지하철_노선_생성_실패됨(response, "노선의 이름이 빈값일 수 없습니다.");
    }

    static Stream<String> blankStrings() {
        return Stream.of("", "   ", null);
    }

    @ParameterizedTest(name = "노선의 색상값이 \"{0}\" 일 경우 지하철 노선을 생성하지 못하고 예외 메시지가 발생한다.")
    @MethodSource("blankStrings")
    void createLineWithEmptyColor(String color) {
        // given
        final Map<String, String> params = getLineCreateParams("미지의이름", color);
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);
        // then
        지하철_노선_생성_실패됨(response, "노선의 색상값이 빈값일 수 없습니다.");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 경우 에러가 발생한다.")
    @Test
    void createLineWithDuplicatedName() {
        // given
        지하철_노선_등록되어_있음("중복될이름", "중복되도되는색상");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_등록되어_있음("중복될이름", "중복되도되는색상");
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
        지하철_노선_목록_포함_검증(response, Arrays.asList(노란노선, 초록노선));
        지하철_노선_목록의_항목_검증(response, Arrays.asList(노란노선, 초록노선));
    }

    @DisplayName("해당 아이디의 지하철 노선이 없는 경우 에러가 발생한다.")
    @Test
    void getLineWithNotFound() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_조회_요청(1225L);
        // then
        지하철_노선_조회가_실패됨(response, "해당 노선의 아이디가 존재하지 않습니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final LineResponse 초록노선 = 지하철_노선_등록되어_있음("초록노선", "초록색").as(LineResponse.class);
        // when
        final ExtractableResponse<Response> response = 지하철_노선_조회_요청(초록노선.getId());
        // then
        지하철_노선_응답됨(response);
        지하철_노선_응답_항목_검증(response, 초록노선);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final LineResponse 파란노선 = 지하철_노선_등록되어_있음("파란노선", "파란색").as(LineResponse.class);
        final Map<String, String> params = getLineCreateParams("초록노선", "초록");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(파란노선.getId(), params);
        // then
        지하철_노선_수정됨(response);
        지하철_노선_수정_시간_검증(response, 파란노선);
    }

    @DisplayName("노선의 이름 빈값일 경우 노선 수정이 실패하며 에러 메시지가 발생한다.")
    @Test
    void updateLineWithEmptyName() {
        // given
        final LineResponse 파란노선 = 지하철_노선_등록되어_있음("파란노선", "파란색").as(LineResponse.class);
        final Map<String, String> params = getLineCreateParams("", "초록");
        // given
        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(파란노선.getId(), params);
        // then
        지하철_노선_수정이_실패됨(response, "노선의 이름이 빈값일 수 없습니다.");
    }


    @DisplayName("노선의 색상값이 빈값일 경우 노선 수정이 실패하며 에러 메시지가 발생한다.")
    @Test
    void updateLineWithEmptyColor() {
        // given
        final LineResponse 파란노선 = 지하철_노선_등록되어_있음("파란노선", "파란색").as(LineResponse.class);
        final Map<String, String> params = getLineCreateParams("초록노선", "");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(파란노선.getId(), params);
        // then
        지하철_노선_수정이_실패됨(response, "노선의 색상값이 빈값일 수 없습니다.");
    }

    @DisplayName("노선의 이름이 이미 존재할 경우 노선 수정이 실패하여 에러 메시지가 발생한다.")
    @Test
    void updateLineWithDuplicatedName() {
        // given
        final LineResponse 파란노선 = 지하철_노선_등록되어_있음("파란노선", "파란색").as(LineResponse.class);
        final Map<String, String> params = getLineCreateParams("파란노선", "색상");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(파란노선.getId(), params);
        // then
        지하철_노선_수정이_실패됨(response, "노선의 이름이 중복되었습니다.");
    }

    @DisplayName("해당 아이디의 노선이 존재하지 않을 경우 에러 메시지가 발생한다.")
    @Test
    void updateLineWithNotFound() {
        // given
        지하철_노선_등록되어_있음("파란노선", "파란색").as(LineResponse.class);
        final Map<String, String> params = getLineCreateParams("초록노선", "초록색");
        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(10L, params);
        // then
        지하철_노선_수정이_실패됨(response, "해당 노선의 아이디가 존재하지 않습니다.");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final LineResponse 파란노선 = 지하철_노선_등록되어_있음("파란노선", "파란색").as(LineResponse.class);
        // when
        final ExtractableResponse<Response> response = 지하철_노선_제거_요청(파란노선.getId());
        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("노선 아이디를 통해서 지하철 노선을 제거할 때 해당 아이디가 없을 경우 에러가 발생한다.")
    @Test
    void deleteLineWithNotFound() {
        // given
        final long id = 1L;
        // when
        final ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);
        // then
        지하철_노선_삭제가_실패됨(response, "해당 노선의 아이디가 존재하지 않습니다.");
    }

    private void 지하철_노선_수정이_실패됨(ExtractableResponse<Response> response, String errorMessage) {
        assertBadRequestAndMessage(response, errorMessage);
    }

    private void 지하철_노선_삭제가_실패됨(ExtractableResponse<Response> response, String errorMessage) {
        assertBadRequestAndMessage(response, errorMessage);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response, String errorMessage) {
        assertBadRequestAndMessage(response, errorMessage);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertHttpStatusOk(response);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertHttpStatusOk(response);
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertHttpStatusOk(response);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertHttpStatusOk(response);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_조회가_실패됨(ExtractableResponse<Response> response, String errorMessage) {
        assertBadRequestAndMessage(response, errorMessage);
    }
}
