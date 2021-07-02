package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.line.LineAcceptance.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        LineRequest lineRequest = new LineRequest("1호선", "blue");

        //when
        //지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        //then
        //지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        //given
        LineRequest lineRequest = new LineRequest("1호선", "blue");
        지하철_노선_생성_요청(lineRequest);

        //when
        //지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        //then
        //지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        //지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("1호선", "blue");
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청(lineRequest);
        //지하철_노선_생성_요청
        lineRequest = new LineRequest("2호선", "green");
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(lineRequest);

        //when
        //지하철_노선_목록_조회
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        //then
        //지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);
        //지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(Arrays.asList(createResponse1, createResponse2), response);
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        //지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("1호선", "blue");
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineRequest);

        //when
        //지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        //then
        //지하철_노선_응답됨
        지하철_노선_응답됨(createResponse, response);
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("1호선", "blue");
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineRequest);

        // when
        // 지하철_노선_수정_요청
        lineRequest = new LineRequest("1호선", "deep blue");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineRequest, createResponse);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(response);
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


}
