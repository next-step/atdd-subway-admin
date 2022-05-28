package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /*
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        int downStationId = 지하철역_생성("신사역").extract().jsonPath().getInt("id");
        int upStationId = 지하철역_생성("광교역").extract().jsonPath().getInt("id");

        // when
        노선_생성("신분당선", "bg-red-600", downStationId, upStationId, 10);

        // then
        String responseLineName = 노션_목록_조회().jsonPath().getString("name");
        assertThat(responseLineName).isEqualTo("신분당선");
    }

    /*
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
        int downStationId = 지하철역_생성("신사역").extract().jsonPath().getInt("id");
        int upStationId = 지하철역_생성("광교역").extract().jsonPath().getInt("id");
        노선_생성("신분당선", "bg-red-600", downStationId, upStationId, 10);

        // when
        List<Object> stationList = 노션_목록_조회().jsonPath().getList("stations");

        // then
        assertThat(stationList).hasSize(2);

    }

    private ExtractableResponse<Response> 노션_목록_조회() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private ValidatableResponse 노선_생성(String name, String color, int upStationId, int downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        return RestAssured
            .given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();
    }

}
