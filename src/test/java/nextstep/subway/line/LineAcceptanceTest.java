package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
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
    private static final String LINE_CREATE_URL = LINE_DEFAULT_URL + "/create";
    private static final String LINE_LIST_URL = LINE_DEFAULT_URL + "/list";

    private LineRequest defaultLine;

    @BeforeEach
    void makeDefaultLineParam() {
        defaultLine = new LineRequest("2호선", "bg-green-600");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = _createLineHandler(defaultLine);

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
        _createLineHandler(defaultLine);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = _createLineHandler(new LineRequest("2호선", "bg-green-600"));

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
        ExtractableResponse<Response> createLineResponse1 = _createLineHandler(defaultLine);

        // 지하철_노선_등록되어_있음 : 신분당선
        ExtractableResponse<Response> createLineResponse2 = _createLineHandler(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINE_LIST_URL)
                .then().log().all()
                .extract();

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
        ExtractableResponse<Response> createLineResponse = _createLineHandler(defaultLine);

        // 생성 요청 Response로부터 Line 정보 가져오기.
        LineResponse createdLine = _getLineResponseByApiResponse(createLineResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> getLineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINE_DEFAULT_URL + "/" + createdLine.getId())
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(getLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createLineResponse = _createLineHandler(defaultLine);

        LineResponse createdLine = _getLineResponseByApiResponse(createLineResponse);

        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "1호선");
        updateParams.put("color", "bg-blue-600");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> updateLineResponse = RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_DEFAULT_URL + "/" + createdLine.getId())
                .then().log().all()
                .extract();

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
        ExtractableResponse<Response> createLineResponse = _createLineHandler(defaultLine);

        LineResponse createdLine = _getLineResponseByApiResponse(createLineResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> updateLineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINE_DEFAULT_URL + "/" + createdLine.getId())
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(updateLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Line 생성 Handler
     * @param line
     * @return
     */
    private ExtractableResponse<Response> _createLineHandler(LineRequest line) {
        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_CREATE_URL)
                .then().log().all()
                .extract();
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
