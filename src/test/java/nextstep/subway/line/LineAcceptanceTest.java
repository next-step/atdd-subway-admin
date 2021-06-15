package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
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
    private static final String PARAM_UP_STATION = "upStationId";
    private static final String PARAM_DOWN_STATION = "downStationId";
    private static final String PARAM_DISTANCE = "distance";
    private static final String 시작_종점 = "1";
    private static final String 도착_종점 = "2";
    private static final String 거리 = "10";
    public static final String STATION_LIST = "stationList";
    public static final String LOCATION = "Location";
    public static final String 강남역 = "강남역";
    public static final String 양재역 = "양재역";

    @BeforeEach
    void setup() {
        역_생성_요청(강남역, 빨간색);
        역_생성_요청(양재역, 빨간색);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 시작_종점, 도착_종점, 거리);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 응답 = 노선생성_요청(신분당선);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(응답);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineDuplicate() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 시작_종점, 도착_종점, 거리);
        노선생성_요청(신분당선);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 노선생성_응답 = 노선생성_요청(신분당선);

        // then
        // 지하철_노선_생성_실패됨
        노선_생성_요청_실패(노선생성_응답);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 시작_종점, 도착_종점, 거리);
        ExtractableResponse<Response> 신분당선_생성_응답 = 노선생성_요청(신분당선);

        Map<String, String> 이호선 = 노선_파라미터_생성(LineAcceptanceTest.이호선, 녹색, 시작_종점, 도착_종점, 거리);
        ExtractableResponse<Response> 이호선_생성_응답 = 노선생성_요청(이호선);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> 노선_목록_조회_응답 = 노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        지하철_노선_응답_정상_검사(노선_목록_조회_응답);
        지하철_노선_목록_포함_검사(신분당선_생성_응답, 이호선_생성_응답, 노선_목록_조회_응답);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 시작_종점, 도착_종점, 거리);
        ExtractableResponse<Response> 신분당선_생성_응답 = 노선생성_요청(신분당선);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(신분당선_생성_응답);

        // then
        // 지하철_노선_응답됨
        노선_이름_색_검사(노선_조회_응답, 신분당선_이름, 빨간색);
    }

    @DisplayName("지하철 노선을 조회하고 출발 시작, 종점 역을 확인한다.")
    @Test
    void getLineNew() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 시작_종점, 도착_종점, 거리);
        ExtractableResponse<Response> 신분당선_생성_응답 = 노선생성_요청(신분당선);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(ID_추출(신분당선_생성_응답));

        // then
        // 지하철_노선_응답됨
        노선_이름_색_검사(노선_조회_응답, 신분당선_이름, 빨간색);
        노선_출발_종점_역_검사(노선_조회_응답, 시작_종점, 도착_종점);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 시작_종점, 도착_종점, 거리);
        ExtractableResponse<Response> 신분당선_생성_응답 = 노선생성_요청(신분당선);

        Map<String, String> 신분당선_수정 = 노선_파라미터_생성(분당선_이름, 노란색, 시작_종점, 도착_종점, 거리);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(신분당선_수정, ID_추출(신분당선_생성_응답));

        // then
        // 지하철_노선_수정됨
        노선_이름_색_검사(노선_수정_응답, 분당선_이름, 노란색);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 시작_종점, 도착_종점, 거리);
        ExtractableResponse<Response> 신분당선_생성_응답 = 노선생성_요청(신분당선);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> 노선_삭제_응답 = 노선_삭제_요청(신분당선_생성_응답);

        // then
        // 지하철_노선_삭제됨
        노선_삭제_검사(노선_삭제_응답);
    }

    private Map<String, String> 노선_파라미터_생성(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> param = new HashMap<>();
        param.put(PARAM_NAME, name);
        param.put(PARAM_COLOR, color);
        param.put(PARAM_UP_STATION, upStationId);
        param.put(PARAM_DOWN_STATION, downStationId);
        param.put(PARAM_DISTANCE, distance);
        return param;
    }

    private ExtractableResponse<Response> 노선생성_요청(Map<String, String> param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_조회_요청(ExtractableResponse<Response> 역_생성_응답) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + 역_생성_응답.body().jsonPath().get("id").toString())
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_조회_요청(String 역_ID) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + 역_ID)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_수정_요청(Map<String, String> 수정_내용, String 역_ID) {
        return RestAssured.given().log().all()
                .body(수정_내용)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + 역_ID)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_삭제_요청(ExtractableResponse<Response> 노선_생성_응답) {
        return RestAssured.given().log().all()
                .when()
                .delete(노선_생성_응답.header(LOCATION))
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_목록_포함_검사(ExtractableResponse<Response> 지하철_생성_응답_1, ExtractableResponse<Response> 지하철_생성_응답_2, ExtractableResponse<Response> 노선_목록_조회_응답) {
        List<Long> expectedLineIds = Arrays.asList(지하철_생성_응답_1, 지하철_생성_응답_2).stream()
                .map(it -> Long.parseLong(it.header(LOCATION).split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = 노선_목록_조회_응답.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> 노선_생성_응답) {
        assertThat(노선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(노선_생성_응답.header(LOCATION)).isNotBlank();
    }

    private void 노선_생성_요청_실패(ExtractableResponse<Response> 노선_생성_실패_응답) {
        assertThat(노선_생성_실패_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 노선_이름_색_검사(ExtractableResponse<Response> 노선_수정_응답, String 분당선이름, String 색) {
        assertThat(노선_수정_응답.body().jsonPath().get(PARAM_NAME).toString()).isEqualTo(분당선이름);
        assertThat(노선_수정_응답.body().jsonPath().get(PARAM_COLOR).toString()).isEqualTo(색);
    }

    private void 노선_삭제_검사(ExtractableResponse<Response> 노선_삭제_응답) {
        assertThat(노선_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 노선_출발_종점_역_검사(ExtractableResponse<Response> 노선_조회_응답, String 시작종점, String 도착종점) {
        List<String> 역_목록 = 노선_조회_응답.jsonPath().getList(STATION_LIST, LineResponse.class).stream()
                .map(it -> it.getName())
                .collect(Collectors.toList());

        List<Long> 역_id = 노선_조회_응답.jsonPath().getList(STATION_LIST, LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(역_목록.size()).isEqualTo(2);
        assertThat(역_id.size()).isEqualTo(2);

        assertThat(역_목록.get(0)).isEqualTo(강남역);
        assertThat(역_목록.get(1)).isEqualTo(양재역);

        assertThat(역_id.get(0)).isEqualTo(Long.parseLong(시작종점));
        assertThat(역_id.get(1)).isEqualTo(Long.parseLong(도착종점));
    }

    private String ID_추출(ExtractableResponse<Response> 응답) {
        return 응답.body().jsonPath().get("id").toString();
    }

    private void 지하철_노선_응답_정상_검사(ExtractableResponse<Response> 노선_목록_조회_응답) {
        assertThat(노선_목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 역_생성_요청(String 이름, String 색) {
        Map<String, String> 요청_내용 = new HashMap<>();
        요청_내용.put(PARAM_NAME, 이름);
        요청_내용.put(PARAM_COLOR, 색);
        RestAssured.given()
                .body(요청_내용)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .extract();
    }
}
