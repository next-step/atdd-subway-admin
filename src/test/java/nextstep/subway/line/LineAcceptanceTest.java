package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceRestAssured.지하철노선_목록_조회;
import static nextstep.subway.line.LineAcceptanceRestAssured.지하철노선_삭제;
import static nextstep.subway.line.LineAcceptanceRestAssured.지하철노선_생성;
import static nextstep.subway.line.LineAcceptanceRestAssured.지하철노선_수정;
import static nextstep.subway.line.LineAcceptanceRestAssured.지하철노선_조회;
import static nextstep.subway.station.StationAcceptanceRestAssured.지하철역들_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.common.BaseAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철노선 생성")
    void createLine() {
        // given
        지하철역들_생성("잠실역", "문정역");

        // when
        ExtractableResponse<Response> createResponse = 지하철노선_생성("8호선", "분홍색", 1L, 2L, 10);
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철노선_목록_조회().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsExactly("8호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록 조회")
    void getLines() {
        // given
        지하철역들_생성("잠실역", "가락시장역", "수서역");
        지하철노선_생성("8호선", "분홍색", 1L, 2L, 10);
        지하철노선_생성("3호선", "노란색", 2L, 3L, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회();

        // then
        JsonPath jsonPath = response.jsonPath();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getList("id", Long.class)).hasSize(2),
                () -> assertThat(jsonPath.getList("name", String.class)).containsExactly("8호선", "3호선"),
                () -> assertThat(jsonPath.getList("stations")).hasSize(2)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 조회")
    void retrieveLine() {
        // given
        지하철역들_생성("잠실역", "문정역");
        Long id = 지하철노선_생성("8호선", "분홍색", 1L, 2L, 10)
                .jsonPath().getLong("id");
        // when
        ExtractableResponse<Response> response = 지하철노선_조회(id);

        // then
        JsonPath jsonPath = response.jsonPath();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(jsonPath.getLong("id")).isEqualTo(id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선 수정")
    void updateLine() {
        // given
        지하철역들_생성("잠실역", "문정역");
        Long id = 지하철노선_생성("8호선", "분홍색", 1L, 2L, 10)
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철노선_수정(1L, "수인분당선", "노란색");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선 삭제")
    void deleteLine() {
        // given
        지하철역들_생성("잠실역", "문정역");
        Long id = 지하철노선_생성("8호선", "분홍색", 1L, 2L, 10)
                .jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철노선_삭제(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
