package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineFixture.requestCreateLine("2호선", "green");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineFixture.requestCreateLine("2호선", "green");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineFixture.requestCreateLine("2호선", "green");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse1 = LineFixture.requestCreateLine("2호선", "green");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse2 = LineFixture.requestCreateLine("3호선", "orange");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineFixture.requestGetLines();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> expectedLines = Arrays.asList(LineFixture.ofLineResponse(createdResponse1), LineFixture.ofLineResponse(createdResponse2));
        List<LineResponse> lines = LineFixture.ofLineResponses(response);
        assertThat(lines).containsAll(expectedLines);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = LineFixture.requestCreateLine("2호선", "green");
        LineResponse createdLineResponse = LineFixture.ofLineResponse(createdResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineFixture.requestGetLineById(createdLineResponse.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = LineFixture.requestCreateLine("2호선", "green");
        LineResponse createdLineResponse = LineFixture.ofLineResponse(createdResponse);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = LineFixture.requestUpdateLine(createdLineResponse.getId(), "3호선", "red");

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = LineFixture.requestCreateLine("2호선", "green");
        LineResponse createdLineResponse = LineFixture.ofLineResponse(createdResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = LineFixture.requestDeleteLine(createdLineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
