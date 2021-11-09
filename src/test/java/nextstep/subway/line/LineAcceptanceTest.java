package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceMethods.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void createLine(String name, String color) {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void createLine2(String name, String color) {
        // given
        지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED,분당선,YELLOW", "1호선,BLUE,2호선,GREEN"})
    void getLines(String firstLineName, String firstLineColor, String secondLineName, String secondLineColor) {
        // given
        LineResponse firstLine = 지하철_노선_등록되어_있음(firstLineName, firstLineColor);
        LineResponse secondLine = 지하철_노선_등록되어_있음(secondLineName, secondLineColor);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(firstLine, secondLine));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void getLine(String name, String color) {
        // given
        LineResponse line = 지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());

        // then
        지하철_노선_응답됨(response);
        지하철_노선_포함됨(response, line);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED,신분당선2,RED2", "분당선,YELLOW,분당선2,YELLOW2", "1호선,BLUE,1호선2,BLUE2"})
    void updateLine(String name, String color, String updateName, String updateColor) {
        // given
        LineResponse line = 지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(line, updateName, updateColor);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @ParameterizedTest
    @CsvSource(value = {"신분당선,RED", "분당선,YELLOW", "1호선,BLUE", "2호선,GREEN"})
    void deleteLine(String name, String color) {
        // given
        LineResponse line = 지하철_노선_등록되어_있음(name, color);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(line);

        // then
        지하철_노선_삭제됨(response);
    }
}
