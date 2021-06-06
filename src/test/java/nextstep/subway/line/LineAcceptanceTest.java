package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final String 신분당선_이름 = "신분당선";
    public static final String 빨간색 = "bg-red-600";
    public static final String 노란색 = "bg-yellow-500";
    public static final String 분당선_이름 = "분당선";
    public static final String 이호선 = "2호선";
    public static final String 녹색 = "bg-green-100";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_COLOR = "color";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> 신분당선 = createNewLine(신분당선_이름, 빨간색);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 응답 = API_요청_POST(신분당선);

        // then
        // 지하철_노선_생성됨
        assertThat(응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(응답.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineDuplicate() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = createNewLine(신분당선_이름, 빨간색);
        API_요청_POST(신분당선);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 응답 = API_요청_POST(신분당선);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = createNewLine(신분당선_이름, 빨간색);
        ExtractableResponse<Response> 신분당선_생성_응답 = API_요청_POST(신분당선);

        Map<String, String> 이호선 = createNewLine(LineAcceptanceTest.이호선, 녹색);
        ExtractableResponse<Response> 이호선_생성_응답 = API_요청_POST(이호선);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> 노선_목록_조회_응답 = API_요청_GET();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(노선_목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(신분당선_생성_응답, 이호선_생성_응답).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = 노선_목록_조회_응답.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = createNewLine(신분당선_이름, 빨간색);
        ExtractableResponse<Response> 신분당선_생성_응답 = API_요청_POST(신분당선);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> 노선_조회_응답 = API_요청_GET(신분당선_생성_응답.body().jsonPath().get("id").toString());

        // then
        // 지하철_노선_응답됨
        assertThat(노선_조회_응답.body().jsonPath().get(PARAM_NAME).toString()).isEqualTo(신분당선_이름);
        assertThat(노선_조회_응답.body().jsonPath().get(PARAM_COLOR).toString()).isEqualTo(빨간색);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = createNewLine(신분당선_이름, 빨간색);
        ExtractableResponse<Response> 신분당선_생성_응답 = API_요청_POST(신분당선);

        Map<String, String> 신분당선_수정 = createNewLine(분당선_이름, 노란색);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> 노선_수정_응답 = API_요청_PUT(신분당선_수정, Integer.toString(신분당선_생성_응답.body().jsonPath().get("id")));

        // then
        // 지하철_노선_수정됨
        assertThat(노선_수정_응답.body().jsonPath().get(PARAM_NAME).toString()).isEqualTo(분당선_이름);
        assertThat(노선_수정_응답.body().jsonPath().get(PARAM_COLOR).toString()).isEqualTo(노란색);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = createNewLine(신분당선_이름, 빨간색);
        ExtractableResponse<Response> 신분당선_생성_응답 = API_요청_POST(신분당선);

        // when
        // 지하철_노선_제거_요청
        String uri = 신분당선_생성_응답.header("Location");
        ExtractableResponse<Response> response = API_요청_DELETE(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> createNewLine(String name, String color) {
        Map<String, String> param = new HashMap<>();
        param.put(PARAM_NAME, name);
        param.put(PARAM_COLOR, color);
        return param;
    }

    private ExtractableResponse<Response> API_요청_POST(Map<String, String> param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> API_요청_GET(String pathVariable) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + pathVariable)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> API_요청_PUT(Map<String, String> param, String pathVariable) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + pathVariable)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> API_요청_GET() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> API_요청_DELETE(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
