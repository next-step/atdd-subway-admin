package nextstep.subway.line;

import java.util.Arrays;
import java.util.List;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_생성_요청("2호선", "green", 1L, 2L, 10);

        // then
        // 지하철_노선_생성됨
        LineAcceptanceTestResponse.지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        String lineName = "2호선";
        String lineColor = "green";
        LineAcceptanceTestRequest.지하철_노선_등록되어_있음(lineName, lineColor, 1L, 2L, 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_생성_요청(lineName, lineColor, 1L, 2L, 10);

        // then
        // 지하철_노선_생성_실패됨
        LineAcceptanceTestResponse.지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        String createdLocationUri = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("2호선", "green", 1L, 2L, 10);
        String createdLocationUri2 = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("3호선", "orange", 3L, 4L, 10);

        List<String> lineLocations = Arrays.asList(createdLocationUri, createdLocationUri2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        LineAcceptanceTestResponse.지하철_노선_목록_응답됨(response);
        LineAcceptanceTestResponse.지하철_노선_목록_포함됨(response, lineLocations);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        String createdLocationUri = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("2호선", "green", 1L, 2L, 10);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_조회_요청(createdLocationUri);

        // then
        // 지하철_노선_응답됨
        LineAcceptanceTestResponse.지하철_노선_응답됨(response, createdLocationUri);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        String createdLocationUri = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("2호선", "green", 1L, 2L, 10);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_수정_요청(createdLocationUri,"3호선", "orange");

        // then
        // 지하철_노선_수정됨
        LineAcceptanceTestResponse.지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        String createdLocationUri = LineAcceptanceTestRequest.지하철_노선_등록되어_있음("2호선", "green", 1L, 2L, 10);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_제거_요청(createdLocationUri);

        // then
        // 지하철_노선_삭제됨
        LineAcceptanceTestResponse.지하철_노선_삭제됨(response);
    }
}
