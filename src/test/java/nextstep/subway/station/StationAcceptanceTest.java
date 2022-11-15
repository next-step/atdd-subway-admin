package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    private static final HashMap<String, String> params = new HashMap<>();

    @BeforeAll
    public static void createParams() {
        params.put("name", "강남역");
    }

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    @DisplayName("지하철역 생성 API 호출")
    private ExtractableResponse<Response> createStation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철역 조회 API 호출")
    private ExtractableResponse<Response> getStations() {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철역_생성후_조회() {
        // when
        ExtractableResponse<Response> response = createStation(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getStations().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void 중복이름의_지하철역_생성_불가() {
        // given
        createStation(params);

        // when
        ExtractableResponse<Response> response = createStation(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void 지하철역_복수개_생성후_목록_조회() {
        //given
        createStation(params);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "잠실역");
        createStation(params2);

        //when
        ExtractableResponse<Response> response = getStations();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", Long.class)).hasSize(2)
        );
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void 생성한_지하철역_삭제_조회불가() {
        //given
        Long id = createStation(params).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> deleteResponse =
                RestAssured.given().log().all()
                        .when().delete("/stations/{id}", id)
                        .then().log().all()
                        .extract();

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        List<Long> stationIds = getStations().jsonPath().getList("id", Long.class);
        assertThat(stationIds).isEmpty();
    }
}
