package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceFixture.LineFixture.*;
import static nextstep.subway.line.LineAcceptanceFixture.PATH;
import static nextstep.subway.line.LineAcceptanceFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void given_NoExisingLine_when_CreateLine_then_ReturnLine() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(PATH, FIRST);

        // then
        assertAll(
            () -> assertThat(statusCode(response)).isEqualTo(statusCode(CREATED)),
            () -> assertThat(response.header("Location")).isNotBlank(),
            () -> assertThat(toLine(response)).isEqualTo(FIRST.line())
        );
    }

    private int statusCode(final ExtractableResponse<Response> response) {
        return response.statusCode();
    }

    private int statusCode(final HttpStatus httpStatus) {
        return httpStatus.value();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void given_ExistingLine_when_CreateLineAlreadyExisting_then_ReturnBadRequest() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(PATH, FIRST);

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(BAD_REQUEST));
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void given_ExistingLine_when_SearchAllLine_then_ReturnLines() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);
        지하철_노선_생성_요청(PATH, SECOND);

        // when
        final ExtractableResponse<Response> response = 지하철_노션_조회_요청(PATH);

        // then
        assertAll(
            () -> assertThat(statusCode(response)).isEqualTo(statusCode(OK)),
            () -> assertThat(actual(response)).isEqualTo(expected())
        );
    }

    private List<Line> actual(final ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getList(".", Line.class);
    }

    private List<Line> expected() {
        return Arrays.asList(FIRST.line(), SECOND.line());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void given_ExistingLine_when_SearchLine_returnLine() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노션_조회_요청(PATH + "/" + FIRST.getId());

        // then
        assertAll(
            () -> assertThat(statusCode(response)).isEqualTo(statusCode(OK)),
            () -> assertThat(toLine(response)).isEqualTo(FIRST.line())
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void given_ExistingLine_when_ModifyLine_then_ReturnOkStatus() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(PATH + "/" + FIRST.getId(), SECOND);

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(OK));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void given_ExistingLine_when_DeleteLine_then_ReturnNoContentStatus() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_제거_요청(PATH + "/" + FIRST.getId());

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(NO_CONTENT));
    }
}
