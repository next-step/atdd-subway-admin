package nextstep.subway.station;

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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역을_생성한다("강남역");

        // then
        지하철역이_생성된다(response);

        // then
        지하철역_목록_조회_시_생성한_역을_찾을_수_있다("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역을_생성한다("강남역");

        // when
        ExtractableResponse<Response> response = 기존에_존재하는_지하철역_이름으로_지하철_역_생성("강남역");

        // then
        지하철역_생성이_안된다(response);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        //given
        지하철역을_2개_생성한다();

        //when
        ExtractableResponse<Response> response = 모든_지하철역을_조회한다();

        //then
        지하철역을_2개_응답받는다(response);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        ExtractableResponse<Response> createResponse = 지하철역을_생성한다("삭제역");

        //when
        지하철역을_제거한다(createResponse);

        //then
        지하철역_목록_조회_시_생성한_역을_찾을_수_없다();
    }

    private static void 지하철역_목록_조회_시_생성한_역을_찾을_수_없다() {
        List<String> stationNames = getList(모든_지하철역을_조회한다(), "name", String.class);
        assertThat(stationNames).doesNotContain("삭제역");
    }

    private ExtractableResponse<Response> 지하철역을_제거한다(ExtractableResponse<Response> createResponse) {
        int stationId = createResponse.body().jsonPath().getInt("id");
        return 지하철역을_제거한다(stationId);
    }

    private ExtractableResponse<Response> 지하철역을_제거한다(int stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + stationId)
                .then().log().all()
                .extract();
    }

    private static void 지하철역_목록_조회_시_생성한_역을_찾을_수_있다(String 생성된_역) {
        List<String> stationNames = getList(모든_지하철역을_조회한다(), "name", String.class);
        assertThat(stationNames).containsAnyOf(생성된_역);
    }

    private static void 지하철역이_생성된다(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.CREATED);
    }

    private static <T> List<T> getList(ExtractableResponse<Response> response, String path, Class<T> mappingType) {
        return response.jsonPath().getList(path, mappingType);
    }

    private static void assertStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    private static ExtractableResponse<Response> 지하철역을_생성한다(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private static void 지하철역_생성이_안된다(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.BAD_REQUEST);
    }

    private static ExtractableResponse<Response> 기존에_존재하는_지하철역_이름으로_지하철_역_생성(String 동일한_이름의_역) {
        return 지하철역을_생성한다(동일한_이름의_역);
    }

    private static void 지하철역을_2개_응답받는다(ExtractableResponse<Response> response) {
        assertStationCount(response, 2);
    }

    private static void 지하철역을_2개_생성한다() {
        지하철역을_생성한다("강남역");
        지하철역을_생성한다("역삼역");
    }

    private static void assertStationCount(ExtractableResponse<Response> response, int stationCount) {
        assertThat(getList(response, "id", Integer.class).size()).isEqualTo(stationCount);
    }

    private static ExtractableResponse<Response> 모든_지하철역을_조회한다() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
    }
}
