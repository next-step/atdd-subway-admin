package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.station.StationApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        지하철노선_생성_성공("2호선", "yellow", "강남역", "교대역", 10);

        // then
        지하철노선_조회_성공("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        지하철노선_생성_성공("1호선", "blue", "영등포역", "신길역", 5);
        지하철노선_생성_성공("2호선", "yellow", "강남역", "교대역", 10);

        // when
        // then
        지하철노선_조회_성공("1호선", "2호선");
    }

    String 지하철노선_생성_성공(String name, String color, String upStationName, String downStationName, long distance) {
        long upStationId = StationApi.create(upStationName).jsonPath().getLong("id");
        long downStationId = StationApi.create(downStationName).jsonPath().getLong("id");
        ExtractableResponse<Response> response = LineApi.create(new LineRequest(name, color, upStationId, downStationId, distance));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response.header("location");
    }

    void 지하철노선_조회_성공(String... names) {
        ExtractableResponse<Response> response = LineApi.findAll();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> findNames = response.jsonPath().getList("name", String.class);
        assertThat(findNames).contains(names);
    }
}
