package nextstep.subway.section;

import static nextstep.subway.Constant.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import nextstep.subway.AcceptanceTest;

@DisplayName("지하철 구간 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {
    public static final String STATION_ID = "stationId";
    public static final String 광교역_이름 = "광교역";
    private static String 강남역_번호;
    private static String 양재역_번호;
    private static String 판교역_번호;

    private static String 에러_메시지_역이_이미_구간_포함됨 = ErrorMessage.STATIONS_ARE_ALREADY_CONTAINS_SECTION;
    private static String 에러_메시지_역을_찾을_수_없음 = ErrorMessage.NOT_FOUND_STATION;
    private static String 에러_메시지_역이_구간에_포함되지_않음 = ErrorMessage.NOT_FOUND_STATIONS_SECTION;
    private static String 에러_메시지_구간이_너무_김 = ErrorMessage.DISTANCE_TOO_LONG;
    private static String 에러_메시지_구간에_포함된_역이_아님 = ErrorMessage.NOT_FOUND_SECTION;
    private static String 에러_미시지_구간이_하나의_구간만_존재 = ErrorMessage.SECTIONS_HAVE_NOT_MIN_COUNT;

    private static ExtractableResponse<Response> 신분당선_생성_응답;

    @BeforeEach
    void setup() {
        강남역_번호 = 번호_추출(역_생성_요청(강남역_이름, 빨간색));
        양재역_번호 = 번호_추출(역_생성_요청(양재역_이름, 빨간색));
        판교역_번호 = 번호_추출(역_생성_요청(판교역_이름, 빨간색));

        신분당선_생성_응답 = 노선생성_요청(노선_파라미터_생성(신분당선_이름, 빨간색, 강남역_번호, 양재역_번호, 구간_길이));
    }

    @DisplayName("구간에 등록하려는 역들이 이미 구간에 포함되어 있다.")
    @Test
    void 지하철_구간_생성_실패_구간에_이미_모두_등록_되어있는_경우() {
        // given

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간_추가_요청_실패_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 양재역_번호, 구간_추가_요청_길이_값);
        ExtractableResponse<Response> 구간_추가_요청_반대_경우_실패_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 양재역_번호, 강남역_번호, 구간_추가_요청_길이_값);

        //then
        //지하철_구간_생성_실패
        구간_추가_요청_실패(구간_추가_요청_실패_응답, 에러_메시지_역이_이미_구간_포함됨);
        구간_추가_요청_실패(구간_추가_요청_반대_경우_실패_응답, 에러_메시지_역이_이미_구간_포함됨);
    }

    @DisplayName("구간에 등록하려는 역이 존재 하지 않는 역의 경우")
    @Test
    void 지하철_구간_생성_실패_구간에_등록하려는_역이_존재_하지_않는_역의_경우() {
        // given

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간_추가_요청_실패_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 존재하지_않는_역_번호, 구간_추가_요청_길이_값);

        //then
        //지하철_구간_생성_실패
        구간_추가_요청_실패(구간_추가_요청_실패_응답, 에러_메시지_역을_찾을_수_없음);
    }

    @DisplayName("지하철 역들이 구간에 모두 등록되지 않은 경우")
    @Test
    void 지하철_역들이_구간에_모두_등록되지_않은_경우() {
        // given
        String 광교역_번호 = 번호_추출(역_생성_요청("광교역", 빨간색));
        String 미금역_번호 = 번호_추출(역_생성_요청("미금역", 빨간색));

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간_추가_요청_실패_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 광교역_번호, 미금역_번호, 구간_추가_요청_길이_값);

        //then
        //지하철_구간_생성_실패
        구간_추가_요청_실패(구간_추가_요청_실패_응답, 에러_메시지_역이_구간에_포함되지_않음);
    }

    @DisplayName("구간 추가시 지정한 거리가 너무 커 추가가 되지 않는다.")
    @Test
    void 구간_추가_거리가_너무_큰_경우() {
        // given

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간_추가_요청_실패_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 판교역_번호, 구간_추가_요청_잘못된_큰_값);

        //then
        //지하철_구간_생성_실패
        구간_추가_요청_실패(구간_추가_요청_실패_응답, 에러_메시지_구간이_너무_김);
    }

    @DisplayName("지하철 구간을 역사이에 추가 한다.")
    @Test
    void 지하철_구간_추가_역_사이() {
        // given

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간추가_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 판교역_번호, 구간_추가_요청_길이_값);

        // then
        // 지하철_구간_생성됨
        지하철_구간_생성됨(구간추가_응답);
        지하철_구간_순서_번호로_검사(구간추가_응답, 강남역_번호, 판교역_번호, 양재역_번호);
        지하철_구간_순서_이름으로_검사(구간추가_응답, 강남역_이름, 판교역_이름, 양재역_이름);
    }

    @DisplayName("지하철 구간을 상단 종점에 한다.")
    @Test
    void 지하철_구간_생성_상단_종점() {
        // given

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간추가_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 판교역_번호, 강남역_번호, 구간_추가_요청_길이_값);

        // then
        // 지하철_구간_생성됨
        지하철_구간_생성됨(구간추가_응답);
        지하철_구간_순서_번호로_검사(구간추가_응답, 판교역_번호, 강남역_번호, 양재역_번호);
        지하철_구간_순서_이름으로_검사(구간추가_응답, 판교역_이름, 강남역_이름, 양재역_이름);
    }

    @DisplayName("지하철 구간을 하단 종점에 한다.")
    @Test
    void 지하철_구간_생성_하단_종점() {
        // given

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간추가_응답 = 구간_추가_요청(번호_추출(신분당선_생성_응답), 양재역_번호, 판교역_번호, 구간_추가_요청_길이_값);

        // then
        // 지하철_구간_생성됨
        지하철_구간_생성됨(구간추가_응답);
        지하철_구간_순서_번호로_검사(구간추가_응답, 강남역_번호, 양재역_번호, 판교역_번호);
        지하철_구간_순서_이름으로_검사(구간추가_응답, 강남역_이름, 양재역_이름, 판교역_이름);
    }

    @DisplayName("지하철 구간 삭제 실패 - 역이 없는 경우")
    @Test
    void deleteSectionFailByNotFoundStation() {
        //given

        // when
        // 지하철_구간_삭제_요청
        ExtractableResponse<Response> 구간삭제_응답 = 구간_삭제_요청(번호_추출(신분당선_생성_응답), 판교역_번호);

        //then
        지하철_구간_삭제_요청_실패(구간삭제_응답, 에러_메시지_구간에_포함된_역이_아님);
    }

    @DisplayName("지하철 구간 삭제 실패 - 구간이 하나만 존재")
    @Test
    void deleteSectionFailedByOnlyOneSection() {
        //given

        // when
        // 지하철_구간_삭제_요청
        ExtractableResponse<Response> 구간삭제_응답 = 구간_삭제_요청(번호_추출(신분당선_생성_응답), 양재역_번호);

        //then
        지하철_구간_삭제_요청_실패(구간삭제_응답, 에러_미시지_구간이_하나의_구간만_존재);
    }

    @DisplayName("지하철 구간 삭제 - 상단 종점")
    @Test
    void deleteSectionFirstStation() {
        //given
        구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 판교역_번호, 구간_추가_요청_길이_값);

        // when
        // 지하철_구간_삭제_요청
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(번호_추출(신분당선_생성_응답), 강남역_번호);

        //then
        지하철_구간_삭제됨(구간_삭제_응답);
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(ID_추출(신분당선_생성_응답));
        지하철_구간_순서_번호로_검사(노선_조회_응답, 판교역_번호, 양재역_번호);
        지하철_구간_순서_이름으로_검사(노선_조회_응답, 판교역_이름, 양재역_이름);
    }

    @DisplayName("지하철 구간 삭제 - 가운데역")
    @Test
    void deleteSectionMiddleStation() {
        //given
        구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 판교역_번호, 구간_추가_요청_길이_값);

        // when
        // 지하철_구간_삭제_요청
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(번호_추출(신분당선_생성_응답), 판교역_번호);

        //then
        지하철_구간_삭제됨(구간_삭제_응답);
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(ID_추출(신분당선_생성_응답));
        지하철_구간_순서_번호로_검사(노선_조회_응답, 강남역_번호, 양재역_번호);
        지하철_구간_순서_이름으로_검사(노선_조회_응답, 강남역_이름, 양재역_이름);
    }

    @DisplayName("지하철 구간 삭제 - 하단 종점")
    @Test
    void deleteSectionLastStation() {
        //given
        구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 판교역_번호, 구간_추가_요청_길이_값);

        // when
        // 지하철_구간_삭제_요청
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(번호_추출(신분당선_생성_응답), 양재역_번호);

        //then
        지하철_구간_삭제됨(구간_삭제_응답);
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(ID_추출(신분당선_생성_응답));
        지하철_구간_순서_번호로_검사(노선_조회_응답, 강남역_번호, 판교역_번호);
        지하철_구간_순서_이름으로_검사(노선_조회_응답, 강남역_이름, 판교역_이름);
    }

    @DisplayName("지하철 구간 삭제 - 4개의 역에서 가운데 제거")
    @Test
    void deleteSection() {
        //given
        String 광교역_번호 = 번호_추출(역_생성_요청(광교역_이름, 빨간색));
        구간_추가_요청(번호_추출(신분당선_생성_응답), 강남역_번호, 판교역_번호, 구간_추가_요청_길이_값);
        구간_추가_요청(번호_추출(신분당선_생성_응답), 양재역_번호, 광교역_번호, 구간_추가_요청_길이_값);

        // when
        // 지하철_구간_삭제_요청
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(번호_추출(신분당선_생성_응답), 판교역_번호);

        //then
        지하철_구간_삭제됨(구간_삭제_응답);
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(ID_추출(신분당선_생성_응답));
        지하철_구간_순서_번호로_검사(노선_조회_응답, 강남역_번호, 양재역_번호, 광교역_번호);
        지하철_구간_순서_이름으로_검사(노선_조회_응답, 강남역_이름, 양재역_이름, 광교역_이름);
    }

    private ExtractableResponse<Response> 역_생성_요청(String 이름, String 색) {
        Map<String, String> 요청_내용 = new HashMap<>();
        요청_내용.put(PARAM_NAME, 이름);
        요청_내용.put(PARAM_COLOR, 색);
        return RestAssured.given()
                .body(요청_내용)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .extract();
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

    private String 번호_추출(ExtractableResponse<Response> 응답) {
        return 응답.body().jsonPath().get("id").toString();
    }

    private ExtractableResponse<Response> 구간_추가_요청(String 역_번호) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/sections/" + 역_번호)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간_추가_요청(String 노선_번호, String 상행_번호, String 하행_번호, String 거리) {
        Map<String, String> param = new HashMap<>();
        param.put(PARAM_UP_STATION, 상행_번호);
        param.put(PARAM_DOWN_STATION, 하행_번호);
        param.put(PARAM_DISTANCE, 거리);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + 노선_번호 + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간_삭제_요청(String 노선_번호, String 역_번호) {
        return RestAssured.given().log().all()
                .param(STATION_ID, Long.parseLong(역_번호))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + 노선_번호 + "/sections")
                .then().log().all()
                .extract();
    }

    private void 지하철_구간_생성됨(ExtractableResponse<Response> 구간_생성_응답) {
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(구간_생성_응답.header(LOCATION)).isNotBlank();
    }

    private void 구간_추가_요청_실패(ExtractableResponse<Response> 구간_추가_요청_실패_응답, String expectMessage) {
        assertThat(HttpStatus.BAD_REQUEST.value()).isEqualTo(구간_추가_요청_실패_응답.statusCode());
        assertThat(expectMessage).isEqualTo(구간_추가_요청_실패_응답.body().jsonPath().get("message"));
    }

    private void 지하철_구간_순서_번호로_검사(ExtractableResponse<Response> 구간_생성_응답, String ...역_번호) {
        List<Long> 역_id = 구간_생성_응답.jsonPath().getList(STATION_LIST, LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(역_id.size()).isEqualTo(역_번호.length);
        for (int 순서 = 0; 순서 < 역_번호.length; 순서++) {
            assertThat(역_id.get(순서)).isEqualTo(Long.parseLong(역_번호[순서]));
        }
    }

    private void 지하철_구간_순서_이름으로_검사(ExtractableResponse<Response> 구간_생성_응답, String ...역_이름) {
        List<String> 역_목록 = 구간_생성_응답.jsonPath().getList(STATION_LIST, LineResponse.class).stream()
                .map(it -> it.getName())
                .collect(Collectors.toList());

        assertThat(역_목록.size()).isEqualTo(역_이름.length);
        for (int 순서 = 0; 순서 < 역_이름.length; 순서++) {
            assertThat(역_목록.get(순서)).isEqualTo(역_이름[순서]);
        }
    }

    private void 지하철_구간_삭제_요청_실패(ExtractableResponse<Response> 구간_삭제_요청_실패_응답, String expectMessage) {
        assertThat(HttpStatus.BAD_REQUEST.value()).isEqualTo(구간_삭제_요청_실패_응답.statusCode());
        assertThat(expectMessage).isEqualTo(구간_삭제_요청_실패_응답.body().jsonPath().get("message"));
    }

    private ExtractableResponse<Response> 노선_조회_요청(String 노선_번호) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + 노선_번호)
                .then().log().all()
                .extract();
    }

    private String ID_추출(ExtractableResponse<Response> 응답) {
        return 응답.body().jsonPath().get("id").toString();
    }

    private void 지하철_구간_삭제됨(ExtractableResponse<Response> 구간_삭제_응답) {
        assertThat(구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
