package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestFixture.*;
import static nextstep.subway.station.StationAcceptanceTestFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.cleanUp();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Long upStationId = 지하철역_생성("김포공항역").jsonPath().getLong("id");
        Long downStationId = 지하철역_생성("여의도역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성("5호선", "보라색", upStationId, downStationId, 13);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철_노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf("5호선");
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        Long upStationId = 지하철역_생성("김포공항역").jsonPath().getLong("id");
        Long downStationId = 지하철역_생성("여의도역").jsonPath().getLong("id");

        지하철_노선_생성("5호선", "보라색", upStationId, downStationId, 13);
        지하철_노선_생성("9호선", "금색", upStationId, downStationId, 13);

        // when
        List<String> lineNames = 지하철_노선_목록_조회().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).hasSize(2)
            .contains("5호선", "9호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        // given
        Long upStationId = 지하철역_생성("김포공항역").jsonPath().getLong("id");
        Long downStationId = 지하철역_생성("여의도역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선_생성("5호선", "보라색", upStationId, downStationId, 13);

        // when
        String lineName = 지하철_노선_조회(response.jsonPath().getLong("id")).jsonPath().getString("name");

        // then
        assertThat(lineName).isEqualTo("5호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        Long upStationId = 지하철역_생성("김포공항역").jsonPath().getLong("id");
        Long downStationId = 지하철역_생성("여의도역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선_생성("5호선", "보라색", upStationId, downStationId, 13);

        // when
        지하철_노선_수정(response.jsonPath().getLong("id"), "9호선", "보라색");

        // then
        ExtractableResponse<Response> result = 지하철_노선_조회(response.jsonPath().getLong("id"));
        assertAll(
            () -> assertThat(result.jsonPath().getString("name")).isEqualTo("9호선"),
            () -> assertThat(result.jsonPath().getString("color")).isEqualTo("보라색")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void removeLine() {
        // given
        Long upStationId = 지하철역_생성("김포공항역").jsonPath().getLong("id");
        Long downStationId = 지하철역_생성("여의도역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선_생성("5호선", "보라색", upStationId, downStationId, 13);

        // when
        지하철_노선_삭제(response.jsonPath().getLong("id"));

        // then
        List<String> lineNames = 지하철_노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames).isEmpty();
    }
}
