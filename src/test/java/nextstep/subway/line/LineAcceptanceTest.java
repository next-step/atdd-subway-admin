package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final LineRequest fourthLine = new LineRequest("4호선", "blue");
    private static final LineRequest newLine = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    private static final String ROOT_REQUEST_URI = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when : 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(fourthLine);

        // then : 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given : 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(fourthLine);

        // when : 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(fourthLine);

        // then : 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> firstCreatedResponse = 지하철_노선_등록되어_있음(fourthLine);

        // 지하철_노선_등록되어_있음
        LineRequest secondLine = new LineRequest("6호선", "brown");
        ExtractableResponse<Response> secondCreatedResponse = 지하철_노선_등록되어_있음(secondLine);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> readAllResponse = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_OK_응답(readAllResponse);

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(firstCreatedResponse, secondCreatedResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = readAllResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given : 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(fourthLine);
        Long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when : 지하철_노선_조회_요청
        ExtractableResponse<Response> readResponse = 지하철_노선_조회_요청(expectedLineId);
        Long respondedLineId = readResponse.jsonPath().getLong("id");

        // then : 지하철_노선_응답됨
        지하철_노선_OK_응답(readResponse);
        assertThat(respondedLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given : 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(fourthLine);
        Long createdLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when : 지하철_노선_수정_요청
        LineRequest request = new LineRequest(fourthLine.getName(), "red");

        ExtractableResponse<Response> updateResponse = 지하철_노선_수정_요청(createdLineId, request);

        // then : 지하철_노선_수정됨
        지하철_노선_OK_응답(updateResponse);
        assertThat(updateResponse.jsonPath().getString("color")).isEqualTo("red");

    }


    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given : 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(fourthLine);
        Long createdLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when : 지하철_노선_제거_요청
        ExtractableResponse<Response> deleteResponse = 지하철_노선_제거_요청(createdLineId);

        // then : 지하철_노선_삭제됨
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }

    @DisplayName("종점역 정보와 함께 지하철 노선을 생성한다.")
    @Test
    void createLineWithSection() {
        // given
        // 지하철_역_등록되어_있음
        // 지하철_역_등록되어_있음

        // when : 지하철_노선_등록되어_있음 - 종점역 포함

        // then
        // 지하철_노선_생성됨 - 종점역 포함
        // 지하철_구간_생성됨
    }

    @DisplayName("지하철역 정보들과 함께 지하철 노선을 조회한다.")
    @Test
    void getLineWithSections() {
        // given : 지하철_노선_등록되어_있음 - 종점역 포함

        // when : 지하철_노선_조회_요청

        // then
        // 지하철_노선_응답됨
        // 지하철_노선의_역목록_조회됨 (상행 → 하행 순정렬)
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest request) {
        return httpPost(ROOT_REQUEST_URI, request);
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return httpGet(ROOT_REQUEST_URI);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return httpGet(ROOT_REQUEST_URI + "/" + id);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return httpDelete(ROOT_REQUEST_URI + "/" + id);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest request) {
        return httpPut(ROOT_REQUEST_URI + "/" + id, request);
    }

    private void 지하철_노선_OK_응답(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
