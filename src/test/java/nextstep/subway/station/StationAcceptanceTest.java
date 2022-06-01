package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
        // given
        final String stationName = "강남역";

        // when
        final ExtractableResponse<Response> response = 지하철역을_생성한다(stationName);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        final List<String> stationNames = 지하철역_목록을_조회한다();
        assertThat(stationNames).containsAnyOf(stationName);
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
        final String stationName = "강남역";
        지하철역을_생성한다(stationName);

        // when
        final ExtractableResponse<Response> response = 지하철역을_생성한다(stationName);

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
    void getStations() {
        // given
        final String gangnam = "강남역";
        지하철역을_생성한다(gangnam);

        final String dongjak = "동작역";
        지하철역을_생성한다(dongjak);

        // when
        final List<String> stationNames = 지하철역_목록을_조회한다();

        // then
        assertThat(stationNames).containsAll(Arrays.asList(gangnam, dongjak));
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
        final String stationName = "강남역";
        final ExtractableResponse<Response> createResponse = 지하철역을_생성한다(stationName);

        // when
        final ExtractableResponse<Response> deleteResponse = 지하철역을_삭제한다(createResponse.jsonPath().getLong("id"));

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        final List<String> stationNames = 지하철역_목록을_조회한다();
        assertThat(stationNames).doesNotContain(stationName);
    }

    private ExtractableResponse<Response> 지하철역을_생성한다(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private List<String> 지하철역_목록을_조회한다() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract()
                .jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> 지하철역을_삭제한다(final Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/stations/{id}", id)
                .then().log().all()
                .extract();
    }
}
