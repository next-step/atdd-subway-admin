package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.Constants.*;
import static nextstep.subway.line.LineAcceptanceRequests.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME);
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

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
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME);
        requestCreateLine(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestNew = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME);
        ExtractableResponse<Response> createResponse1 = requestCreateLine(lineRequestNew);
        LineRequest lineRequestSecond = new LineRequest(SECOND_LINE_COLOR, SECOND_LINE_NAME);
        ExtractableResponse<Response> createResponse2 = requestCreateLine(lineRequestSecond);
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestShowLines();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME);
        requestCreateLine(lineRequest);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = requestShowLine("1");

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestNew = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME);
        ExtractableResponse<Response> createResponse = requestCreateLine(lineRequestNew);

        // when
        // 지하철_노선_수정_요청
        String uri = createResponse.header("Location");
        LineRequest lineRequestOld = new LineRequest(OLD_BUNDANG_LINE_COLOR, OLD_BUNDANG_LINE_NAME);
        ExtractableResponse<Response> response = requestUpdateLine(uri,lineRequestOld);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestNew = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME);
        ExtractableResponse<Response> createResponse = requestCreateLine(lineRequestNew);

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestDeleteLine(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
