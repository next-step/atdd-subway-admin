package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.application.DatabaseCleanup;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.PutLineRequest;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철노선을 생성한다.")
    void 지하철노선_등록() {
        Long upStationId = 지하철역_생성("지하철역");
        Long downStationId = 지하철역_생성("새로운지하철역");

        // When
        LineRequest shinBundang = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId, 10);
        ExtractableResponse<Response> response = 지하철노선_생성(shinBundang);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // Then
        List<String> lineNames = 지하철노선_전체_목록_조회().jsonPath().getList("name", String.class);

        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("모든 지하철 노선을 생성한다.")
    void 지하철노선_목록_조회() {
        Long upStationId = 지하철역_생성("지하철역");
        Long downStationId1 = 지하철역_생성("새로운지하철역");
        Long downStationId2 = 지하철역_생성("또다른지하철역");

        // Given
        LineRequest shinBundang = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId1, 10);
        LineRequest bundang = new LineRequest("분당선", "bg-green-600", upStationId, downStationId2, 10);
        지하철노선_생성(shinBundang);
        지하철노선_생성(bundang);

        // When
        List<Long> lineIds = 지하철노선_전체_목록_조회().jsonPath().getList("id", Long.class);

        // Then
        assertThat(lineIds).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("아이디로 지하철노선을 조회한다.")
    void 지하철노선_조회() {
        Long upStationId = 지하철역_생성("지하철역");
        Long downStationId1 = 지하철역_생성("새로운지하철역");

        // Given
        LineRequest shinBundang = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId1, 10);
        Long lineId = 지하철노선_생성(shinBundang).jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> response = 지하철노선_조회(lineId);

        // Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(lineId),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(shinBundang.getName()),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo(shinBundang.getColor()));
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("특정 지하철노선의 정보를 수정할 수 있다.")
    void 지하철노선_수정() {
        Long upStationId = 지하철역_생성("지하철역");
        Long downStationId1 = 지하철역_생성("새로운지하철역");

        // Given
        LineRequest shinBundang = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId1, 10);
        Long lineId = 지하철노선_생성(shinBundang).jsonPath().getLong("id");

        // When
        PutLineRequest request = new PutLineRequest("다른분당선", "bg-red-600");
        ExtractableResponse<Response> response = 지하철노선_수정(lineId, request);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // Then
        String name = 지하철노선_조회(lineId).jsonPath().getString("name");
        assertThat(name).isEqualTo("다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("특정 지하철노선을 삭제할 수 있다.")
    void 지하철노선_삭제() {
        Long upStationId = 지하철역_생성("지하철역");
        Long downStationId1 = 지하철역_생성("새로운지하철역");

        // Given
        LineRequest shinBundang = new LineRequest("신분당선", "bg-red-600", upStationId, downStationId1, 10);
        Long lineId = 지하철노선_생성(shinBundang).jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> response = 지하철노선_삭제(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // Then
        response = 지하철노선_조회(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철노선_생성(LineRequest request) {
        return RestAssured.given().log().all()
                .body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정(Long lineId, PutLineRequest request) {
        return RestAssured.given().log().all()
                .body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_삭제(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private Long 지하철역_생성(String stationName) {
        ExtractableResponse<Response> response = Station.createStation(stationName);
        return response.jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> 지하철노선_전체_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }
}
