package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
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
class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 2개를 생성하면
     * Then 지하철역 2개가 생성된다
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     * When 지하철역 목록 조회시
     * Then 2개의 지하철역을 응답 받는다
     * When 2개 중 1개 지하철역을 삭제하면
     * Then 그 지하철역은 목록 조회 시 생성한 역을 찾을 수 없다
     * */
    @DisplayName("지하철역을 관리한다.")
    @Test
    void manageStations(){
        // when
        Map<String, String> params1 = new HashMap<>();
        Map<String, String> params2 = new HashMap<>();
        params1.put("name", "강남역");
        params2.put("name", "서울역");

        // then
        for (ExtractableResponse<Response> createResponse : requestCreateStations(Arrays.asList(params1, params2))) {
            assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        // when
        ExtractableResponse<Response> createResponse = requestCreateStation(params1);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // when
        ExtractableResponse<Response> getAllResponse = requestGetStations();
        assertThat(getAllResponse.jsonPath().getList("name")).contains("강남역");

        //then
        assertAll(
                () -> assertThat(getAllResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(getAllResponse.jsonPath().getList("id")).contains(1, 2),
                () -> assertThat(getAllResponse.jsonPath().getList("name")).contains("강남역", "서울역")
        );

        //when
        assertThat(requestDeleteStation(1L).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        ExtractableResponse<Response> response = requestGetStations();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id")).doesNotContain(1),
                () -> assertThat(response.jsonPath().getList("name")).doesNotContain("강남역"),

                () -> assertThat(response.jsonPath().getList("id")).contains(2),
                () -> assertThat(response.jsonPath().getList("name")).contains("서울역")
        );
    }

    private List<ExtractableResponse<Response>> requestCreateStations(List<Map<String, String>> stationsParamsBundle) {
        List<ExtractableResponse<Response>> responses = new ArrayList<>();
        for (Map<String, String> stationParams : stationsParamsBundle) {
            responses.add(requestCreateStation(stationParams));
        }
        return responses;
    }

    private ExtractableResponse<Response> requestCreateStation(Map<String, String> stationParams) {
        return RestAssured.given().log().all()
                .body(stationParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGetStations() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestDeleteStation(long stationId) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when().delete("/stations/{id}", stationId)
                .then().log().all()
                .extract();
    }
}
