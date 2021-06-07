package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineAcceptanceStep.*;

@DisplayName("지하철 노선 관련 기능") //Feature
public class LineAcceptanceTest extends AcceptanceTest {

    //Background

    @DisplayName("지하철 노선을 생성한다.") //Scenario
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("1호선", "blue");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.") //Scenario
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음("1호선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("1호선", "blue");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.") //Scenario
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> expected1 = 지하철_노선_등록되어_있음("1호선", "blue");
        ExtractableResponse<Response> expected2 = 지하철_노선_등록되어_있음("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(expected1, expected2, response);
    }


    @DisplayName("지하철 노선을 조회한다.") //Scenario
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> expected = 지하철_노선_등록되어_있음("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(expected);

        // then
        지하철_노선_응답됨(expected, response);
    }


    @DisplayName("지하철 노선을 수정한다.") //Scenario
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> givenResponse = 지하철_노선_등록되어_있음("1호선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(givenResponse, "1호선", "pink");

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.") //Scenario
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> givenResponse = 지하철_노선_등록되어_있음("1호선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(givenResponse);

        // then
        지하철_노선_삭제됨(response);
    }
}
