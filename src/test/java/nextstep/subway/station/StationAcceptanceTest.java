package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
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
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
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
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        //given
        지하철역만들기("강남역");
        지하철역만들기("양재역");

        //when
        ExtractableResponse<Response> response = 지하철역조회하기();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("$"))
            .extracting("id")
            .containsExactly(1, 2);
        assertThat(response.jsonPath().getList("$"))
            .extracting("name")
            .containsExactly("강남역", "양재역");
    }

    private ExtractableResponse<Response> 지하철역조회하기() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/stations")
            .then().log().all()
            .extract();
        return response;
    }

    private void 지하철역만들기(String 생성할_지하철역_이름) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 생성할_지하철역_이름);
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        지하철역만들기("강남역"); //id 1
        지하철역만들기("역삼역"); //id 2

        지하철역지우기(1);

        ExtractableResponse<Response> response = 지하철역조회하기();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("$"))
            .extracting("id")
            .containsExactly(2);
        assertThat(response.jsonPath().getList("$"))
            .extracting("name")
            .containsExactly("역삼역");
    }

    private void 지하철역지우기(long 지하철역_id) {
        RestAssured.given().log().all()
            .body("")
            .when().delete("/stations/" + 지하철역_id)
            .then().log().all();
    }
}
