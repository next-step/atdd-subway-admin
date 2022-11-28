package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StationAcceptanceTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철역을 생성한다.")
    public void createStation_success() {
        // given
        final String stationName = "강남역";

        // when
        final ExtractableResponse<Response> response = createStation(stationName).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        final List<String> stationNames = getStationNames();
        assertThat(stationNames).contains(stationName);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성할 수 없다.")
    public void createStationWithDuplicateName_badRequest() {
        // given
        final String stationName = "강남역";
        createStation(stationName);

        // when
        final ExtractableResponse<Response> response = createStation(stationName).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @Test
    @DisplayName("지하철역을 조회한다.")
    public void getStations_ok() {
        // given
        final String stationName1 = "강남역";
        createStation(stationName1);
        final String stationName2 = "역삼역";
        createStation(stationName2);

        // when
        final ExtractableResponse<Response> response = getStations().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<String> stationNames = getStationNames();
        assertThat(stationNames).contains(stationName1, stationName2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @Test
    @DisplayName("지하철역을 제거한다.")
    public void deleteStation_notFoundInList() {
        // given
        final String targetStationName = "강남역";
        final Long createdId = createStationResponse(targetStationName).as(StationResponse.class).getId();

        // given
        deleteStation(createdId).extract();

        // when
        final ExtractableResponse<Response> response = getStations().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        final List<String> stationNames = getStationNames();
        assertThat(stationNames).doesNotContain(targetStationName);
    }

    public static Response createStationResponse(String value) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", value);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations");
    }

    private ValidatableResponse createStation(String value) {
        return createStationResponse(value).then().log().all();
    }

    private ValidatableResponse getStations() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all();
    }

    private List<String> getStationNames() {
        return getStations().extract().jsonPath().getList("name", String.class);
    }

    private ValidatableResponse deleteStation(long stationId) {
        return RestAssured.given().log().all()
                .pathParam("id", stationId)
                .when().delete("/stations/{id}")
                .then().log().all();
    }
}
