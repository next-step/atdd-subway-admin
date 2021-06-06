package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.type.LineColor;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static nextstep.subway.line.LineAcceptanceTestSteps.*;
import static nextstep.subway.line.LineAcceptanceTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = send_createLine(makeLineRequest("신분당선", LineColor.RED));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicate() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = makeLineRequest("신분당선", LineColor.RED);
        send_createLine_with_success_check(request);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = send_createLine(request);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    static LineResponse send_createLine_with_success_check(final LineRequest request) {
        ExtractableResponse<Response> response = send_createLine(request);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return convertToLineResponse(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        LineResponse expect_1 = send_createLine_with_success_check(makeLineRequest("신분당선", LineColor.RED));
        LineResponse expect_2 = send_createLine_with_success_check(makeLineRequest("2호선", LineColor.GREEN));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = send_getLines();
        
        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(containsToResponse(Arrays.asList(expect_1, expect_2), convertToLineResponses(response))).isTrue();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse expected = send_createLine_with_success_check(makeLineRequest("신분당선", LineColor.RED));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = send_getLine(expected.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse actual = convertToLineResponse(response);
        assertAll(() -> {
            assertThat(actual.getName()).isEqualTo("신분당선");
            assertThat(actual.getColor()).isEqualTo(LineColor.RED);
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse expected = send_createLine_with_success_check(makeLineRequest("신분당선", LineColor.RED));

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = send_updateLine(expected.getId(), makeLineRequest("구분당선", LineColor.BLUD));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());;
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse expected = send_createLine_with_success_check(makeLineRequest("신분당선", LineColor.RED));

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = send_deleteLine(expected.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
