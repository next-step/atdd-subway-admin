package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.helper.TestIsolator;
import nextstep.subway.station.StationAcceptanceTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    private TestIsolator testIsolator;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        testIsolator.run();
    }

    /**
     *  When 지하철 노선을 생성하면
     *  Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        StationAcceptanceTestFixture.createStation("지하철역");
        StationAcceptanceTestFixture.createStation("새로운지하철역");
        LineAcceptanceTestFixture.createLine("신분당선", "bg-red-600", 10, 1, 2);

        // then
        ExtractableResponse<Response> findAllResponse = LineAcceptanceTestFixture.findAllLines();
        List<String> lineNames = findAllResponse.jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf("신분당선");
    }
}
