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
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    public static final String ENDPOINT = "/stations";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철역_생성() {
        // when
        ExtractableResponse<Response> response = createStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getStationsIn("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하려 하면
     * Then 지하철역 생성이 안 된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하려 한다.")
    @Test
    void 지하철역_중복_생성() {
        // given
        createStation("강남역");

        // when
        ExtractableResponse<Response> response = createStation("강남역");

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
    void 지하철역_목록_조회() {
        // given
        createStation("여의나루역");
        createStation("안국역");

        // when
        List<String> stationNames = getStationsIn("name", String.class);

        // then
        assertThat(stationNames).containsExactly("여의나루역", "안국역");
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
        Long stationId = createStation("여의나루역")
                .jsonPath().getLong("id");

        // when
        deleteStation(stationId);

        // then
        List<String> stationNames = getStationsIn("name", String.class);
        assertThat(stationNames).doesNotContain("여의나루역");
    }

    /**
     * When 존재하지 않는 지하철역을 삭제하려 하면
     * Then 404 에러가 전달된다
     */
    @DisplayName("존재하지 않는 지하철역을 삭제하려 한다.")
    @Test
    void 존재하지_않는_지하철역_삭제() {
        // when
        ExtractableResponse<Response> response = deleteStation(1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static ExtractableResponse<Response> createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(ENDPOINT)
                .then().log().all()
                .extract();
    }

    private <T> List<T> getStationsIn(String path, Class<T> genericType) {
        return RestAssured.given().log().all()
                .when().get(ENDPOINT)
                .then().log().all()
                .extract().jsonPath().getList(path, genericType);
    }

    private ExtractableResponse<Response> deleteStation(Long stationId) {
        return RestAssured.given().log().all()
                .when().delete(ENDPOINT + "/" + stationId)
                .then().log().all()
                .extract();
    }
}
