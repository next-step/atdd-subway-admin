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
    Long stationId1;
    Long stationId2;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        stationId1 = StationApi.create("강남역").jsonPath().getLong("id");
        stationId2 = StationApi.create("교대역").jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        지하철노선_생성_성공(new LineRequest("2호선", "yellow", stationId1, stationId2, 10));

        // then
        지하철노선_조회_성공("2호선");
    }

    String 지하철노선_생성_성공(LineRequest lineRequest) {
        ExtractableResponse<Response> response = LineApi.create(lineRequest);
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
