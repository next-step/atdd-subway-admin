package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTest.신규_역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }


    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        ExtractableResponse<Response> 상행_방향_역 = 신규_역_등록("강남역");
        ExtractableResponse<Response> 하행_방향_역 = 신규_역_등록("양재역");

        // when
        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", Long.parseLong(상행_방향_역.jsonPath().getString("id")));
        params.put("downStationId", Long.parseLong(하행_방향_역.jsonPath().getString("id")));
        params.put("distance", 10);

        ExtractableResponse<Response> response = 노선_생성(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> 목록_조회_결과_응답 = 노선_목록_조회();
        assertThat(목록_조회_결과_응답.jsonPath().getList(".")).hasSize(1);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void showLines() {
        // Given
        ExtractableResponse<Response> line1_상행_방향_역 = 신규_역_등록("강남역");
        ExtractableResponse<Response> line1_하행_방향_역 = 신규_역_등록("양재역");

        Map<String, Object> line1Params = new HashMap<>();
        line1Params.put("name", "신분당선");
        line1Params.put("color", "bg-red-600");
        line1Params.put("upStationId", Long.parseLong(line1_상행_방향_역.jsonPath().getString("id")));
        line1Params.put("downStationId", Long.parseLong(line1_하행_방향_역.jsonPath().getString("id")));
        line1Params.put("distance", 10);

        노선_생성(line1Params);

        ExtractableResponse<Response> line2_상행_방향_역 = 신규_역_등록("신림역");
        ExtractableResponse<Response> line2_하행_방향_역 = 신규_역_등록("서원역");

        Map<String, Object> line2Params = new HashMap<>();
        line2Params.put("name", "신림선");
        line2Params.put("color", "bg-red-100");
        line2Params.put("upStationId", Long.parseLong(line2_상행_방향_역.jsonPath().getString("id")));
        line2Params.put("downStationId", Long.parseLong(line2_하행_방향_역.jsonPath().getString("id")));
        line2Params.put("distance", 7);

        노선_생성(line2Params);

        // when
        ExtractableResponse<Response> 노선_목록_조회_결과 = 노선_목록_조회();

        // then
        assertThat(노선_목록_조회_결과.jsonPath().getList("name")).hasSize(2);
    }

    protected static ExtractableResponse<Response> 노선_목록_조회() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
    }

    protected static ExtractableResponse<Response> 노선_생성(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }
}
