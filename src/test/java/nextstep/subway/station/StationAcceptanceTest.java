package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.RestAssuredSetUp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@Sql(scripts = "classpath:truncateTables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest extends RestAssuredSetUp {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_등록("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철_역_전체조회().jsonPath().getList("name", String.class);
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
        지하철_역_등록("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_등록("강남역");

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
        지하철_역_등록("강남역");
        지하철_역_등록("논현역");

        //when
        ExtractableResponse<Response> response = 지하철_역_전체조회();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name")).hasSize(2),
                () -> assertThat(response.jsonPath().getList("name")).containsExactlyInAnyOrder("강남역", "논현역")
        );
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
        지하철_역_등록("강남역");
        long removeStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");

        //when
        지하철_역_삭제(removeStationId);
        ExtractableResponse<Response> response = 지하철_역_전체조회();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name")).hasSize(1),
                () -> assertThat(response.jsonPath().getList("name")).containsExactlyInAnyOrder("강남역")
        );
    }

    public static ExtractableResponse<Response> 지하철_역_등록(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_역_전체조회() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    private void 지하철_역_삭제(long id) {
        RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }
}
