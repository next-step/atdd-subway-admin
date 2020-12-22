package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.line.step.LineAcceptanceStepTest.*;
import static nextstep.subway.line.step.LineAcceptanceStepTest.지하철_노선_응답됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> extract = 지하철_노선_생성_요청("신분당선", "bg-red-600");

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(extract);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineExistLineName() {
        // given
        // 지하철_노선_등록되어_있음
        String lineName = "신분당선";
        지하철_노선_등록되어_있음(lineName, "bg-red-600");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineName, "bg-red-600");

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        List<Long> responses = new ArrayList<>();
        // given
        // 지하철_노선_등록되어_있음
        responses.add(지하철_노선_등록되어_있음("신분당선", "bg-red-600"));
        // 지하철_노선_등록되어_있음
        responses.add(지하철_노선_등록되어_있음("2호선", "bg-orange-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(responses, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        long lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        long lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        // 지하철_노선_수정_요청
        String editName = "3호선";
        String editColor = "bg-orange";
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineId, editName, editColor);
        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(response, editName, editColor);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        long lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineId);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

}
