package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleaner;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    private Station upStation;
    private Station downStation;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        upStation = stationRepository.save(new Station("상행지하철역"));
        downStation = stationRepository.save(new Station("하행지하철역"));
    }

    @AfterEach
    public void tearDown() {
        databaseCleaner.clean();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        //when
        ExtractableResponse<Response> response =
                createLine("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getAllLine().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록을 조회한다.")
    void getAll() {
        // given
        ExtractableResponse<Response> response1 =
                createLine("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);
        ExtractableResponse<Response> response2 =
                createLine("새로운추가노선", "bg-orange-500", upStation.getId(), downStation.getId(), 20L);
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        List<String> stationNames = getAllLine().jsonPath().getList("name", String.class);

        // then
        assertThat(stationNames).containsAnyOf("신분당선", "새로운추가노선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선을 조회한다.")
    void getOne() {
        // given
        ExtractableResponse<Response> response =
                createLine("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        List<String> stationNames = getAllLine().jsonPath().getList("name", String.class);

        // then
        assertThat(stationNames).containsExactly("신분당선");
    }

    private ExtractableResponse<Response> getAllLine() {
        return RestAssured.given().log().all().when().get("/lines").then().log().all().extract();
    }

    private ExtractableResponse<Response> createLine(
            String name, String color, long upStationId, long downStationId, long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all().body(params).contentType(MediaType.APPLICATION_JSON_VALUE).when()
                .post("/lines").then().log().all().extract();
    }

}
