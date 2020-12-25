package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.RequestTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RequestTest.doPost("/lines", new LineRequest("2호선", "초록"));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest requestBody = new LineRequest("2호선", "초록");
        RequestTest.doPost("/lines", requestBody);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RequestTest.doPost("/lines", requestBody);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = RequestTest.doPost("/lines", new LineRequest("2호선", "초록"));
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2 = RequestTest.doPost("/lines", new LineRequest("1호선", "파랑"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RequestTest.doGet("/lines");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> result = response.jsonPath()
                .getList(".", LineResponse.class);

        assertThat(result).contains(createResponse.as(LineResponse.class), createResponse2.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = RequestTest.doPost("/lines", new LineRequest("2호선", "초록"));
        LineResponse createdLineResponse = createResponse.as(LineResponse.class);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RequestTest.doGet("/lines/" + createdLineResponse.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_정보_확인
        LineResponse selectedLineResponse = response.as(LineResponse.class);
        assertThat(selectedLineResponse).isEqualTo(createdLineResponse);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = RequestTest.doPost("/lines", new LineRequest("2호선", "초록"));
        LineResponse lineResponse = createResponse.as(LineResponse.class);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = RequestTest.doPut("/lines/" + lineResponse.getId(), new LineRequest("2호선", "빨강"));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = RequestTest.doPost("/lines", new LineRequest("2호선", "초록"));
        LineResponse lineResponse = createResponse.as(LineResponse.class);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = RequestTest.doDelete("/lines/" + lineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
