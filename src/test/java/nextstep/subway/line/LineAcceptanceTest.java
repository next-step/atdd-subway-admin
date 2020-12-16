package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.step.LineAcceptanceStep.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        String lineName = "9호선";
        String lineColor = "금색";

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(lineName, lineColor);

        // then
        // 지하철_노선_생성됨
        NEW_LINE_CREATED(response, lineName, lineColor);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        String lineName = "9호선";
        String lineColor = "금색";

        // given
        // 지하철_노선_등록되어_있음
        LINE_ALREADY_CREATED(lineName, lineColor);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = REQUEST_CREATE_NEW_LINE(lineName, lineColor);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        String line1Name = "9호선";
        String line2Name = "2호선";
        String line1Color = "금색";
        String line2Color = "초록색";
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line1CreatedResponse
                = LINE_ALREADY_CREATED(line1Name, line1Color);
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line2CreatedResponse
                = LINE_ALREADY_CREATED(line2Name, line2Color);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = REQUEST_LINES();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        LINES_INCLUDED_IN_LIST(line1CreatedResponse, line2CreatedResponse, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        String lineName = "9호선";
        String lineColor = "금색";
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = LINE_ALREADY_CREATED(lineName, lineColor);
        Long createdLineId = EXTRACT_ID_FROM_RESPONSE_LOCATION(createdResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = REQUEST_ONE_SPECIFIC_LINE(createdLineId);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLineWhenNotExist() {
        Long notExistId = 0L;

        ExtractableResponse<Response> response = REQUEST_ONE_SPECIFIC_LINE(notExistId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        String lineName = "비 내리는 호남선";
        String lineColor = "남행열차색";
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> response = LINE_ALREADY_CREATED(lineName, lineColor);

        // when
        // 지하철_노선_수정_요청
        Long lineId = EXTRACT_ID_FROM_RESPONSE_LOCATION(response);
        LineRequest lineRequest = new LineRequest(lineName, lineColor);
        ExtractableResponse<Response> resultResponse = REQUEST_LINE_UPDATE(lineId, lineRequest);

        // then
        // 지하철_노선_수정됨
        assertThat(resultResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateWithNotExistLine() {
        Long notExistId = 4L;
        LineRequest lineRequest = new LineRequest("notExist", "notExist");
        // given
        // 등록된_지하철_노선_없음

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = REQUEST_LINE_UPDATE(notExistId, lineRequest);

        // then
        // 지하철_노선_찾을수_없음
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        String lineName = "2020호선";
        String lineColor = "무지개색";
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = LINE_ALREADY_CREATED(lineName, lineColor);
        Long createdId = EXTRACT_ID_FROM_RESPONSE_LOCATION(createdResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = REQUEST_LINE_DELETE(createdId);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거 시도 시 실패")
    @Test
    void deleteLineFailTest() {
        Long notExistLineId = 4L;
        // given
        // 지하철_노선_등록되어_있지_않음

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = REQUEST_LINE_DELETE(notExistLineId);

        // then
        // 지하철_노선_제거_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
