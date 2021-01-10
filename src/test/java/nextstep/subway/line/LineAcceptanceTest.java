package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // then
        // 지하철_노선_생성됨
        지하철_노선_응답(response, HttpStatus.CREATED);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_응답(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");
        LineAcceptanceUtil.지하철_노선_생성_요청("2호선", "bg-green-600");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_목록_조회();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        지하철_노선_응답(response, HttpStatus.OK);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // when
        // 지하철_노선_조회_요청
        // when
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_조회(1);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답(response, HttpStatus.OK);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // when
        // 지하철_노선_수정_요청
        // when
        ExtractableResponse<Response> response =  LineAcceptanceUtil.지하철_노선_수정_요청(1, "2호선", "bg-green-600");

        // then
        // 지하철_노선_수정됨
        지하철_노선_응답(response, HttpStatus.OK);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // when
        // 지하철_노선_제거_요청
        // when
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_제거_요청(1);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_응답(response, HttpStatus.NO_CONTENT);
    }

    private void 지하철_노선_응답(ExtractableResponse<Response> response, HttpStatus status) {
        Assertions.assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
