package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineAcceptanceStep.*;
import static nextstep.subway.station.StationAcceptanceStep.지하철역_등록되어_있음;

@DisplayName("지하철 노선 관련 기능") //Feature
public class LineAcceptanceTest extends AcceptanceTest {

    //Background
    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철역_등록되어_있음("용산역");
        지하철역_등록되어_있음("서울역");
    }

    @DisplayName("지하철 노선을 생성할 때 두 종점역을 추가하여 생성한다.") //Scenario
    @Test
    void createSectionsLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("1호선", "blue", 1L,2L, 10);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.") //Scenario
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음("1호선", "blue", 1L,2L, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("1호선", "blue", 1L,2L, 10);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.") //Scenario
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> expected1 = 지하철_노선_등록되어_있음("1호선", "blue", 1L,2L, 10);
        ExtractableResponse<Response> expected2 = 지하철_노선_등록되어_있음("2호선", "green", 1L,2L, 10);

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
        ExtractableResponse<Response> expected = 지하철_노선_등록되어_있음("2호선", "green", 1L,2L, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(expected);

        // then
        지하철_노선_응답됨(expected, response);
    }

    @DisplayName("지하철 노선을 수정한다.") //Scenario
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> givenResponse = 지하철_노선_등록되어_있음("1호선", "blue", 1L,2L, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(givenResponse, "1호선", "pink");

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.") //Scenario
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> givenResponse = 지하철_노선_등록되어_있음("1호선", "blue", 1L,2L, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(givenResponse);

        // then
        지하철_노선_삭제됨(response);
    }
}
