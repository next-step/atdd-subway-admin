package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.util.LineTestUtil;
import nextstep.subway.util.StationTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;
    ExtractableResponse<Response> lineCreateResponse;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        Long upStationId = StationTestUtil.createStation("강남역").jsonPath().getLong("id");
        Long downStationId = StationTestUtil.createStation("광교중앙역").jsonPath().getLong("id");
        lineCreateResponse = LineTestUtil.createLine("신분당선", "red", upStationId, downStationId, 10);
    }

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // BeforeEach 에서 실행

        // then
        assertThat(lineCreateResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> response = LineTestUtil.getAllLines();
        assertThat(response.jsonPath().getList("name", String.class)).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long upStationIdOfBundang = StationTestUtil.createStation("수원역").jsonPath().getLong("id");
        Long downStationIdOfBundang = StationTestUtil.createStation("압구정로데오").jsonPath().getLong("id");
        LineTestUtil.createLine("분당선", "yellow", upStationIdOfBundang, downStationIdOfBundang, 10);

        // when
        ExtractableResponse<Response> response = LineTestUtil.getAllLines();

        // then
        assertThat(response.jsonPath().getList("$", LineResponse.class)).hasSize(2);
        assertThat(response.jsonPath().getList("name", String.class)).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // BeforeEach 에서 실행

        // when
        LineResponse lineResponse = LineTestUtil.getLine(lineCreateResponse.jsonPath().getLong("id")).jsonPath()
                .getObject("$", LineResponse.class);

        // then
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // BeforeEach 에서 실행

        // when
        LineTestUtil.updateLine(lineCreateResponse.jsonPath().getLong("id"), "짱비싼선", "black");

        // then
        LineResponse lineResponse = LineTestUtil.getLine(lineCreateResponse.jsonPath().getLong("id")).jsonPath()
                .getObject("$", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("짱비싼선");
        assertThat(lineResponse.getColor()).isEqualTo("black");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        // BeforeEach에서 실행

        // when
        LineTestUtil.deleteLine(lineCreateResponse.jsonPath().getLong("id"));

        // then
        ExtractableResponse<Response> response = LineTestUtil.getAllLines();
        assertThat(response.jsonPath().getList("name", String.class)).doesNotContain("신분당선");
    }
}
