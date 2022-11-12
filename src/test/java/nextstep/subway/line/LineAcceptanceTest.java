package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestFixture.createLine;
import static nextstep.subway.line.LineAcceptanceTestFixture.findAllLines;
import static nextstep.subway.station.StationAcceptanceTestFixture.createStation;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.helper.JsonPathExtractor;
import nextstep.subway.helper.TestIsolator;
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
    void createLineTest() {
        // when
        Long upStationId = JsonPathExtractor.getId(createStation("지하철역"));
        Long downStationId = JsonPathExtractor.getId(createStation("새로운지하철역"));
        createLine("신분당선", "bg-red-600", 10, upStationId, downStationId);

        // then
        List<String> lineNames = JsonPathExtractor.getNames(findAllLines());
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     *  Given 2개의 지하철 노선을 생성하고
     *  When 지하철 노선 목록을 조회하면
     *  Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long upStationId = JsonPathExtractor.getId(createStation("지하철역"));
        Long downStationId =  JsonPathExtractor.getId(createStation("새로운지하철역"));
        createLine("신분당선", "bg-red-600", 10, upStationId, downStationId);
        Long anotherDownStationId =  JsonPathExtractor.getId(createStation("또다른지하철역"));
        createLine("분당선", "bg-green-600", 20, upStationId, anotherDownStationId);

        // when
        ExtractableResponse<Response> findAllResponse = findAllLines();

        // then
        assertThat(JsonPathExtractor.getTotalJsonArraySize(findAllResponse)).isEqualTo(2);
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 생성한 지하철 노선을 조회하면
     *  Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        long upStationId = createStation("지하철역").jsonPath().getLong("id");
        long downStationId = createStation("새로운지하철역").jsonPath().getLong("id");
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600", 10, upStationId, downStationId);

        // when
        Long createStationId = createResponse.jsonPath().getLong("id");
        ExtractableResponse<Response> findLineResponse = RestAssured.given().log().all()
                .when().get("/lines/{id}", createStationId)
                .then().log().all()
                .extract();

        // then
        assertThat(JsonPathExtractor.getTotalJsonArraySize(findLineResponse)).isEqualTo(1);
        assertThat(findLineResponse.jsonPath().getString("name")).isEqualTo("신분당선");
    }
}
