package nextstep.subway.Section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.노선_신분당선;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_ID;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    private Long 양재역_ID;
    private Long 판교역_ID;
    private Long 신분당선_ID;
    private Map<String, String> 사이_구간;
    private Map<String, String> 상행_구간;
    private Map<String, String> 하행_구간;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        양재역_ID = 지하철_역_ID(양재역);
        판교역_ID = 지하철_역_ID(판교역);
        노선_신분당선 = new HashMap() {{
            put("name", "신분당선");
            put("color", "red");
            put("upStationId", 양재역_ID);
            put("downStationId", 판교역_ID);
            put("distance", 10);
        }};
        신분당선_ID = 지하철_노선_ID(노선_신분당선);
    }

    @DisplayName("노선 사이에 구간을 등록한다.")
    @Test
    void addSection() {
        //given
        사이_구간 = 지하철_노선_사이_역_등록(지하철_역_ID(청계산입구));

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_생성_요청(사이_구간, 신분당선_ID);

        // then
        지하철_노선_구간_생성됨(response);
    }

    @DisplayName("노선에 상행 구간을 등록한다.")
    @Test
    void addSection2() {
        //given
        상행_구간 = 지하철_노선_상행_역_등록(지하철_역_ID(강남역));

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_생성_요청(상행_구간, 신분당선_ID);

        // then
        지하철_노선_구간_생성됨(response);
    }

    @DisplayName("노선에 하행 구간을 등록한다.")
    @Test
    void addSection3() {
        //given
        하행_구간 = 지하철_노선_하행_역_등록(지하철_역_ID(광교역));

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_생성_요청(하행_구간, 신분당선_ID);

        // then
        지하철_노선_구간_생성됨(response);
    }

    private HashMap 지하철_노선_하행_역_등록(Long id) {
        return new HashMap() {{
            put("upStationId", 판교역_ID);
            put("downStationId", id);
            put("distance", 4);
        }};
    }

    private HashMap 지하철_노선_상행_역_등록(Long id) {
        return new HashMap() {{
            put("upStationId", id);
            put("downStationId", 양재역_ID);
            put("distance", 4);
        }};
    }

    private HashMap 지하철_노선_사이_역_등록(Long id) {
        return new HashMap() {{
            put("upStationId", id);
            put("downStationId", 판교역_ID);
            put("distance", 4);
        }};
    }

    private void 지하철_노선_구간_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> 지하철_노선_구간_생성_요청(Map<String, String> params, Long id) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}", id)
                .then()
                .log().all()
                .extract();
    }
}
