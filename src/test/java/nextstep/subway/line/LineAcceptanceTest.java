package nextstep.subway.line;

import static nextstep.subway.line.LineTestUtil.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final String LINES_PATH = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");

        // when
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성됨(신분당선_생성_응답);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void duplicateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);

        // when
        ExtractableResponse<Response> 중복_결과_응답 = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(중복_결과_응답);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);
        // 지하철_노선_등록되어_있음
        LineRequest 공항철도 = 지하철_노선_정보("공항철도", "blue");
        ExtractableResponse<Response> 공항철도_생성_응답 = 지하철_노선_생성_요청(공항철도);

        // when
        ExtractableResponse<Response> 노선_목록_응답 = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_응답됨(노선_목록_응답);
        지하철_노선_목록_포함됨(노선_목록_응답, 신분당선_생성_응답, 공항철도_생성_응답);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);
        Long 신분당선_생성_아이디 = 신분당선_생성_응답.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 신분당선_조회_응답 = 지하철_노선_조회_요청(신분당선_생성_아이디);

        // then
        지하철_노선_응답됨(신분당선_조회_응답);
    }

    @DisplayName("존재하지 않는 지하철 노선 조회")
    @Test
    void notFoundLine() {
        // given
        // 존재하지 않는 지하철 노선
        Long 존재하지_않는_지하철_노선_아이디 = -1L;

        // when
        ExtractableResponse<Response> 존재하지_않는_지하철_노선_조회_응답 = 지하철_노선_조회_요청(존재하지_않는_지하철_노선_아이디);

        // then
        지하철_노선_존재하지_않음(존재하지_않는_지하철_노선_조회_응답);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);
        Long 신분당선_생성_아이디 = 신분당선_생성_응답.jsonPath().getLong("id");
        LineRequest 공항철도 = 지하철_노선_정보("공항철도", "blue");

        // when
        ExtractableResponse<Response> 신분당선_수정_응답 = 지하철_노선_수정_요청(신분당선_생성_아이디, 공항철도);

        // then
        지하철_노선_응답됨(신분당선_수정_응답);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void notFoundLineUpdate() {
        // given
        // 지하철_노선_존재하지_않음
        Long 존재하지_않는_노선_아이디 = -1L;
        LineRequest 경의중앙선 = 지하철_노선_정보("경의중앙선", "green");

        // when
        ExtractableResponse<Response> 신분당선_수정_응답 = 지하철_노선_수정_요청(존재하지_않는_노선_아이디, 경의중앙선);

        // then
        지하철_노선_존재하지_않음(신분당선_수정_응답);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "red");
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철_노선_생성_요청(신분당선);

        // when
        ExtractableResponse<Response> 신분당선_제거_응답 = 지하철_노선_제거_요청(신분당선_생성_응답);

        // then
        지하철_노선_삭제됨(신분당선_제거_응답);
    }

}
