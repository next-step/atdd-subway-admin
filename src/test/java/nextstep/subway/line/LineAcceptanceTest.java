package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.util.LineAcceptanceMethods;
import nextstep.subway.util.StationAcceptanceMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends BaseAcceptanceTest {

    private ExtractableResponse<Response> lineCreateResponse;

    @BeforeEach
    @Order(1)
    public void setUp() {
        Long upStationId = StationAcceptanceMethods.createStation("강남역").jsonPath().getLong("id");
        Long downStationId = StationAcceptanceMethods.createStation("광교중앙역").jsonPath().getLong("id");
        lineCreateResponse = LineAcceptanceMethods.createLine("신분당선", "red", upStationId, downStationId, 10);
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
        ExtractableResponse<Response> response = LineAcceptanceMethods.getAllLines();
        assertThat(response.jsonPath().getList("name", String.class)).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Long upStationIdOfBundang = StationAcceptanceMethods.createStation("수원역").jsonPath().getLong("id");
        Long downStationIdOfBundang = StationAcceptanceMethods.createStation("압구정로데오").jsonPath().getLong("id");
        LineAcceptanceMethods.createLine("분당선", "yellow", upStationIdOfBundang, downStationIdOfBundang, 10);

        // when
        ExtractableResponse<Response> response = LineAcceptanceMethods.getAllLines();

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
        LineResponse lineResponse = LineAcceptanceMethods.getLine(lineCreateResponse.jsonPath().getLong("id")).jsonPath()
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
        LineAcceptanceMethods.updateLine(lineCreateResponse.jsonPath().getLong("id"), "짱비싼선", "black");

        // then
        LineResponse lineResponse = LineAcceptanceMethods.getLine(lineCreateResponse.jsonPath().getLong("id")).jsonPath()
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
        LineAcceptanceMethods.deleteLine(lineCreateResponse.jsonPath().getLong("id"));

        // then
        ExtractableResponse<Response> response = LineAcceptanceMethods.getAllLines();
        assertThat(response.jsonPath().getList("name", String.class)).doesNotContain("신분당선");
    }
}
