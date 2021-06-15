package nextstep.subway.section;

import static nextstep.subway.Constant.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.HashMap;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import nextstep.subway.AcceptanceTest;

@DisplayName("지하철 구간 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static String 강남역_번호;
    private static String 양재역_번호;
    private static String 판교역_번호;

    @BeforeEach
    void setup() {
        강남역_번호 = ID_추출(역_생성_요청(강남역_이름, 빨간색));
        양재역_번호 = ID_추출(역_생성_요청(양재역_이름, 빨간색));
        판교역_번호 = ID_추출(역_생성_요청(판교역_이름, 빨간색));
    }

    @Test
    @DisplayName("지하철 구간을 상단 종점에 한다.")
    void 지하철_구간_생성_상단_종점() {
        // given
        Map<String, String> 신분당선 = 노선_파라미터_생성(신분당선_이름, 빨간색, 강남역_번호, 양재역_번호, 거리);
        ExtractableResponse<Response> 신분당선_생성_응답 = 노선생성_요청(신분당선);

        // when
        // 지하철_구간_생성_요청
        ExtractableResponse<Response> 구간추가_응답 = 구간추가_요청(ID_추출(신분당선_생성_응답), 판교역_번호, 강남역_번호, "5");

        // then
        // 지하철_구간_생성됨
        지하철_구간_생성됨(구간추가_응답);
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

    private String ID_추출(ExtractableResponse<Response> 응답) {
        return 응답.body().jsonPath().get("id").toString();
    }

    private ExtractableResponse<Response> 구간추가_요청(String 역_번호) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/sections/" + 역_번호)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간추가_요청(String 노선_번호, String 상행_번호, String 하행_번호, String 거리) {
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

    private void 지하철_구간_생성됨(ExtractableResponse<Response> 구간_생성_응답) {
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(구간_생성_응답.header(LOCATION)).isNotBlank();
    }
}
