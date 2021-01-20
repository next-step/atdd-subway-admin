package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given, when
        ExtractableResponse<Response> response =
                LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("1호선", "파란색"));

        // then
        // 지하철_노선_생성됨
        LineResponseTestModule.응답코드_확인(response, HttpStatus.CREATED.value());
        LineResponseTestModule.컨텐츠유형_확인(response, MediaType.APPLICATION_JSON_VALUE);
        LineResponseTestModule.지하철_노선_생성및조회_검증(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("1호선", "파란색"));

        // when
        ExtractableResponse<Response> response = 
                LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("1호선", "파란색"));

        // then
        // 지하철_노선_생성_실패됨
        LineResponseTestModule.응답코드_확인(response, HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("1호선", "파란색"));
        LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("2호선", "초록색"));
        
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response =
                LineRequestTestModule.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        LineResponseTestModule.응답코드_확인(response, HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        LineResponseTestModule.지하철_노선_목록_포함_검증(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createLineResponse =
                LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("1호선", "파란색"));

        // when
        ExtractableResponse<Response> response =
                LineRequestTestModule.지하철_노선_조회_요청(createLineResponse);

        // then
        // 지하철_노선_응답됨
        LineResponseTestModule.응답코드_확인(response, HttpStatus.OK.value());
        LineResponseTestModule.지하철_노선_생성및조회_검증(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void patchLine() {
        // given
        ExtractableResponse<Response> createLineResponse =
                LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("1호선", "파란색"));

        // when
        LineRequestTestModule.지하철_노선_수정_요청(new LineRequest(
                        Long.parseLong(createLineResponse.jsonPath().getString("id")), "2호선", "초록색"),
                createLineResponse);
        ExtractableResponse<Response> response = LineRequestTestModule.지하철_노선_조회_요청(createLineResponse);

        // then
        // 지하철_노선_수정됨
        LineResponseTestModule.응답코드_확인(response, HttpStatus.OK.value());
        LineResponseTestModule.지하철_노선_수정_검증(response, createLineResponse);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createLineResponse =
                LineRequestTestModule.지하철_노선_생성_요청(new LineRequest("1호선", "파란색"));

        // when
        ExtractableResponse<Response> response =
                LineRequestTestModule.지하철_노선_제거_요청(createLineResponse);
        ExtractableResponse<Response> getOneLineResponse =
                LineRequestTestModule.지하철_노선_조회_요청(createLineResponse);

        // then
        // 지하철_노선_삭제됨
        LineResponseTestModule.응답코드_확인(response, HttpStatus.NO_CONTENT.value());
        LineResponseTestModule.응답코드_확인(getOneLineResponse, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
