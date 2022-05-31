package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends BaseAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성하면 조회 시 해당 노선을 찾을 수 있다.")
    @Test
    void createLine() {
        // when
        StationResponse upStation = createStationRequest("강남역").jsonPath().getObject("", StationResponse.class);
        StationResponse donwStation = createStationRequest("광교역").jsonPath().getObject("", StationResponse.class);

        LineRequest lineRequest = LineRequest.of("신분당선", "bg-red-600", upStation.getId(), donwStation.getId(), 10);
        ExtractableResponse<Response> response = createLineRequest(lineRequest);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());

        // then
        LineResponse lineResponse = findLineRequest(1L).jsonPath().getObject("", LineResponse.class);
        assertThat(lineResponse)
                .satisfies(line -> {
                    assertThat(line.getName())
                            .isEqualTo(lineRequest.getName());
                    assertThat(line.getColor())
                            .isEqualTo(lineRequest.getColor());
                    assertThat(line.getStations())
                            .containsExactly(upStation, donwStation);
                });
    }


    private ExtractableResponse<Response> createLineRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLineRequest(long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("생성된 전체 지하철 노선 목록을 조회할 수 있다.")
    @Test
    void getLines() {
        // when

        // then

        // then
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("생성된 특정 지하철 노선을 조회할 수 있다.")
    @Test
    void getLine() {
        // when

        // then

        // then
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 정보를 수정할 수 있다.")
    @Test
    void updateLine() {
        // when

        // then

        // then
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 정보를 삭제할 수 있다.")
    @Test
    void deleteLine() {
        // when

        // then

        // then
    }
}
