package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();

        StationAcceptanceTest.createStation("지하철역");
        StationAcceptanceTest.createStation("새로운지하철역");
        StationAcceptanceTest.createStation("또다른지하철역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void requestApiByCreateLine() {
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        ValidatableResponse response = requestApiByCreateLine(request);

        assertStatusCode(response, HttpStatus.CREATED);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void findAllLines () {
        requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));
        requestApiByCreateLine(new LineRequest("분당선", "bg-red-600", 1L, 3L, 10));

        ValidatableResponse response = requestApiByFindAllLines();

        assertThat(extractLineNames(response)).containsAnyOf("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLine () {
        Long lineId = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10))
            .extract()
            .as(LineResponse.class)
            .getId();

        ValidatableResponse response = requestApiByGetLine(lineId);

        assertThat(extractLineName(response)).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine () {
        Long lineId = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10))
            .extract()
            .as(LineResponse.class)
            .getId();

        ValidatableResponse response = requestApiByUpdateLine(lineId, new LineUpdateRequest("다른분당선", "bg-red-600"));

        assertStatusCode(response, HttpStatus.OK);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("노선 정보를 삭제한다.")
    @Test
    void deleteLine () {
        Long lineId = requestApiByCreateLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10))
            .extract()
            .as(LineResponse.class)
            .getId();

        ValidatableResponse response = requestApiByDeleteLine(lineId);

        assertStatusCode(response, HttpStatus.NO_CONTENT);
        assertThat(extractLineNames(requestApiByFindAllLines())).doesNotContain("신분당선");
    }

    private static ValidatableResponse requestApiByCreateLine(LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();
    }

    private static ValidatableResponse requestApiByFindAllLines() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all();
    }

    private static ValidatableResponse requestApiByGetLine(long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/{id}", id)
            .then().log().all();
    }

    private static ValidatableResponse requestApiByUpdateLine(long id, LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
            .body(lineUpdateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{id}", id)
            .then().log().all();
    }

    private static ValidatableResponse requestApiByDeleteLine(long id) {
        return RestAssured.given().log().all()
            .when().delete("/lines/{id}", id)
            .then().log().all();
    }


    private static void assertStatusCode(ValidatableResponse response, HttpStatus httpStatus) {
        assertThat(response.extract().statusCode()).isEqualTo(httpStatus.value());
    }

    private static List<String> extractLineNames(ValidatableResponse response) {
        return response.extract().jsonPath().getList("name", String.class);
    }

    private static String extractLineName(ValidatableResponse response) {
        return response.extract().jsonPath().getString("name");
    }
}
