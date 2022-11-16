package nextstep.subway.line;

import static nextstep.subway.line.LineTestFixture.*;
import static nextstep.subway.station.StationTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

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
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createLine() {
        // give
        JsonPath upStation = requestCreateStation("지하철역").jsonPath();
        JsonPath downStation = requestCreateStation("새로운지하철역").jsonPath();
        // when
        ExtractableResponse<Response> response =
                requestCreateLine("신분당선","bg-red-600",upStation.getLong("id"), downStation.getLong("id"),10);
        // then
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(lineResponse.getStations()).hasSize(2)
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철역 노선 목록을 조회한다.")
    @Test
    void findAllLine() {
        // give
        JsonPath station = requestCreateStation("지하철역").jsonPath();
        JsonPath newStation = requestCreateStation("새로운지하철역").jsonPath();
        JsonPath otherStation = requestCreateStation("또다른지하철역").jsonPath();
        requestCreateLine("신분당선","bg-red-600",station.getLong("id"), newStation.getLong("id"),10);
        requestCreateLine("분당선","bg-green-600",station.getLong("id"), otherStation.getLong("id"),10);
        // when
        ExtractableResponse<Response> response = requestGetAllLine();
        // then
        List<LineResponse> lines = response.jsonPath().getList(".",LineResponse.class);
        assertThat(lines).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철역 노선을 조회한다.")
    @Test
    void findLine() {
        // give
        JsonPath station = requestCreateStation("지하철역").jsonPath();
        JsonPath newStation = requestCreateStation("새로운지하철역").jsonPath();
        ExtractableResponse<Response> createdLine =
                requestCreateLine("신분당선","bg-red-600",station.getLong("id"), newStation.getLong("id"),10);
        // when
        ExtractableResponse<Response> response = requestGetLine(createdLine.jsonPath().getLong("id"));
        // then
        LineResponse line = response.jsonPath().getObject(".",LineResponse.class);
        assertThat(line.equals(createdLine.jsonPath().getObject(".", LineResponse.class))).isTrue();
    }


}
