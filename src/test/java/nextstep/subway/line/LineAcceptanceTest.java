package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineUpdateRequest;
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
        // When
        ExtractableResponse<Response> 신분당선_생성_응답 = 신분당선_생성();
        assertThat(신분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // Then
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
        신분당선_생성();
        신림선_생성();

        // When
        ExtractableResponse<Response> 노선_목록_조회_결과 = 노선_목록_조회();

        // Then
        assertThat(노선_목록_조회_결과.jsonPath().getList("name")).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void showLine() {
        // Given
        ExtractableResponse 신분당선_생성_응답 = 신분당선_생성();

        // When
        Integer 신분당선_ID = 신분당선_생성_응답.jsonPath().get("id");
        ExtractableResponse<Response> 노선_조회_결과 = 노선_조회(Long.valueOf(신분당선_ID));

        // Then
        assertThat(노선_조회_결과.jsonPath().getString("name")).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        // Given
        ExtractableResponse 신분당선_생성_응답 = 신분당선_생성();

        // When
        Long 신분당선_ID = Long.valueOf((Integer) 신분당선_생성_응답.jsonPath().get("id"));
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest( "다른분당선", "bg-100-1234");
        ExtractableResponse<Response> 노선_수정_결과 = 노선_수정(신분당선_ID, lineUpdateRequest);

        // Then
        assertThat(노선_수정_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 노선_수정(Long id, LineUpdateRequest lineUpdateRequest) {
        return RestAssured
                .given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 노선_조회(Long id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all().extract();
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

    private ExtractableResponse<Response> 신분당선_생성() {
        ExtractableResponse<Response> 신분당선_상행_방향_역 = 신규_역_등록("강남역");
        ExtractableResponse<Response> 신분당선_하행_방향_역 = 신규_역_등록("양재역");

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", Long.parseLong(신분당선_상행_방향_역.jsonPath().getString("id")));
        params.put("downStationId", Long.parseLong(신분당선_하행_방향_역.jsonPath().getString("id")));
        params.put("distance", 10);

        return 노선_생성(params);
    }

    private ExtractableResponse<Response> 신림선_생성() {
        ExtractableResponse<Response> 신림선_상행_방향_역 = 신규_역_등록("신림역");
        ExtractableResponse<Response> 신림선_하행_방향_역 = 신규_역_등록("서원역");

        Map<String, Object> params = new HashMap<>();
        params.put("name", "신림선");
        params.put("color", "bg-red-100");
        params.put("upStationId", Long.parseLong(신림선_상행_방향_역.jsonPath().getString("id")));
        params.put("downStationId", Long.parseLong(신림선_하행_방향_역.jsonPath().getString("id")));
        params.put("distance", 7);

        return 노선_생성(params);
    }
}
