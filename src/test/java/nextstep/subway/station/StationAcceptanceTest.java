package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.db.DataInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void cleanUp() {
        dataInitializer.execute("station");
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
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
                RestAssured.given().log().all()
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
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
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();

        // when
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();

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
        // given, when
        ExtractableResponse<Response> response = 지하철역_생성("대림역");
        ExtractableResponse<Response> response2 = 지하철역_생성("역삼역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> result = 지하철역_조회();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(convertToStationNames(result).size()).isEqualTo(2);
        assertThat(convertToStationNames(result)).containsAnyOf("대림역", "역삼역");
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
        ExtractableResponse<Response> saveResponse = 지하철역_생성("대림역");
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        long 대림역_ID = saveResponse.body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse =
            RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + 대림역_ID)
                .then().log().all()
                .extract();

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // when
        ExtractableResponse<Response> result = 지하철역_조회();
        List<String> stationNames = convertToStationNames(result);

        // then
        assertFalse(stationNames.contains("대림역"));
        assertThat(stationNames.size()).isEqualTo(0);
    }

    /**
     * 전달받은 지하철역 목록을 저장한다
     * @param names 지하철역 이름 목록
     */
    ExtractableResponse<Response> 지하철역_생성(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    /**
     * 지하철역 목록을 조회한다
     */
    ExtractableResponse<Response> 지하철역_조회() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    private List<String> convertToStationNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }
}
