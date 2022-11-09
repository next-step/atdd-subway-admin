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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        ExtractableResponse<Response> response = 지하철역을_생성("강남역");

        // then
        지하철역이_생성됨(response);

        // then
        생성한_역이_포함됨(지하철역_목록_조회(), "강남역");
    }

    private ExtractableResponse<Response> 지하철역을_생성(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();
    }

    private void 지하철역이_생성됨(ExtractableResponse<Response> response) {
        assertEquals(HttpStatus.CREATED.value(), response.statusCode());
    }

    private List<String> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
                    .when().get("/stations")
                    .then().log().all()
                    .extract().jsonPath().getList("name", String.class);
    }

    private void 생성한_역이_포함됨(List<String> stationNames, String expectedName) {
        assertThat(stationNames).containsAnyOf(expectedName);
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
        지하철역을_생성("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역을_생성("강남역");

        // then
        지하철역_생성이_안됨(response);
    }

    private void 지하철역_생성이_안됨(ExtractableResponse<Response> response) {
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> response1 = 지하철역을_생성("강남역");
        ExtractableResponse<Response> response2 = 지하철역을_생성("역삼역");

        // when
        List<String> stationNames = 지하철역_목록_조회();

        // then
        모든_지하철역이_생성됨(Arrays.asList(response1, response2));

        // then
        생성한_모든_역이_포함됨(stationNames, Arrays.asList("강남역", "역삼역"));
    }

    private void 모든_지하철역이_생성됨(List<ExtractableResponse<Response>> responses) {
        assertThat(responses)
                .allMatch((response -> response.statusCode() == HttpStatus.CREATED.value()));
    }

    private void 생성한_모든_역이_포함됨(List<String> stationNames, List<String> expectedNames) {
        assertThat(stationNames).containsAll(expectedNames);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Long id = 생성된_지하철역의_ID를_얻음(지하철역을_생성("강남역"));

        // when
        ExtractableResponse<Response> response = 지하철역을_삭제(id);

        // then
        지하철역이_삭제됨(response);

        // then
        생성한_역을_찾을_수_없음(지하철역_목록_조회(), "강남역");
    }

    private Long 생성된_지하철역의_ID를_얻음(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> 지하철역을_삭제(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/stations/" + id)
                .then().log().all()
                .extract();
    }

    private void 지하철역이_삭제됨(ExtractableResponse<Response> response) {
        assertEquals(response.statusCode(), HttpStatus.NO_CONTENT.value());
    }

    private void 생성한_역을_찾을_수_없음(List<String> stationNames, String createdStationName) {
        assertThat(stationNames).doesNotContain(createdStationName);
    }
}
