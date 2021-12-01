package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LINE_DEFAULT_URL = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest defaultLine = _getLineRequest("2호선", "bg-green-600");
        ExtractableResponse<Response> response = _createLine(defaultLine);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest defaultLine = _getLineRequest("2호선", "bg-green-600");
        _createLine(defaultLine);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = _createLine(defaultLine);

        // then
        // 지하철_노선_생성_실패됨
        // INTERNAL_SERVER_ERROR : 500
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음 : 2호선
        LineRequest defaultLine = _getLineRequest("2호선", "bg-green-600");
        ExtractableResponse<Response> createLineResponse1 = _createLine(defaultLine);

        // 지하철_노선_등록되어_있음 : 신분당선
        ExtractableResponse<Response> createLineResponse2 = _createLine(_getLineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = _getLines();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createLineResponse1, createLineResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest defaultLine = _getLineRequest("2호선", "bg-green-600");
        ExtractableResponse<Response> createLineResponse = _createLine(defaultLine);

        // 생성 요청 Response로부터 Line 정보 가져오기.
        LineResponse createdLine = _getLineResponseByApiResponse(createLineResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> getLineResponse = _getLine(createdLine.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(getLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest defaultLine = _getLineRequest("2호선", "bg-green-600");
        ExtractableResponse<Response> createLineResponse = _createLine(defaultLine);

        LineResponse createdLine = _getLineResponseByApiResponse(createLineResponse);

        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "1호선");
        updateParams.put("color", "bg-blue-600");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> updateLineResponse = _updateLine(createdLine.getId(), updateParams);

        LineResponse updatedLine = _getLineResponseByApiResponse(updateLineResponse);

        // then
        // 지하철_노선_수정됨
        assertAll(
                () -> assertThat(updateLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedLine.getId()).isEqualTo(createdLine.getId()),
                () -> assertThat(updatedLine.getName()).isEqualTo((String) updateParams.get("name")),
                () -> assertThat(updatedLine.getColor()).isEqualTo((String) updateParams.get("color"))
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest defaultLine = _getLineRequest("2호선", "bg-green-600");
        ExtractableResponse<Response> createLineResponse = _createLine(defaultLine);

        LineResponse createdLine = _getLineResponseByApiResponse(createLineResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> updateLineResponse = _deleteLine(createdLine.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(updateLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * 지하철 Line 생성 요청
     * @param line
     * @return
     */
    private ExtractableResponse<Response> _createLine(LineRequest line) {
        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 Line 목록 조회 요청
     * @return
     */
    private ExtractableResponse<Response> _getLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINE_DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 Line 정보 조회 요청
     * @param lineId
     * @return
     */
    private ExtractableResponse<Response> _getLine(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINE_DEFAULT_URL + "/" + lineId)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 Line 정보 수정 요청
     * @param lineId
     * @param updateParams
     * @return
     */
    private ExtractableResponse<Response> _updateLine(Long lineId, Map<String, String> updateParams) {
        return RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_DEFAULT_URL + "/" + lineId)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 Line 삭제
     * @param lineId
     * @return
     */
    private ExtractableResponse<Response> _deleteLine(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINE_DEFAULT_URL + "/" + lineId)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 LineRequestion(객체) 가져오기
     * @param name
     * @param color
     * @return
     */
    private LineRequest _getLineRequest(String name, String color) {
        return new LineRequest(name, color);
    }

    /**
     * API를 통해 전달받은 Response를 통해 LineResponse 객체 가져오기
     * @param response
     * @return
     */
    private LineResponse _getLineResponseByApiResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class);
    }
}
