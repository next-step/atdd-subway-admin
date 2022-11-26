package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.line.LineAcceptanceTestFixture.*;
import static nextstep.subway.acceptance.station.StationAcceptanceTestFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = 지하철역_생성("강남역").jsonPath().getLong("id");
        downStationId = 지하철역_생성("판교역").jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // then
        assertThat(지하철_노선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        지하철_노선_목록에_생성한_노선이_포함되어_있다(지하철_노선_이름_전체_목록(), "신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("2개의 지하철 노선을 생성하고 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.")
    @Test
    void getLines() {
        // given
        지하철_노선_생성("2호선", "bg-red-600", upStationId, downStationId, 15);
        지하철_노선_생성("9호선", "bg-green-600", upStationId, downStationId, 10);

        // when
        List<String> 지하철_노선_이름_전체_목록 = 지하철_노선_이름_전체_목록();

        // then
        지하철_노선_목록에_생성한_노선이_포함되어_있다(지하철_노선_이름_전체_목록, "2호선", "9호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 생성하고 지하철 노선 목록을 조회한다.")
    @Test
    void createLineAndGetLines() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성 = 지하철_노선_생성("2호선", "bg-red-600", upStationId, downStationId, 15);

        // when
        ExtractableResponse<Response> 지하철_노선_조회 = 지하철_노선_조회(지하철_노선_생성.jsonPath().getLong("id"));

        // then
        지하철_노선_이름을_검증한다(지하철_노선_조회.jsonPath().getString("name"), "2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 생성하고 노선명, 노선색을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성 = 지하철_노선_생성("2호선", "bg-red-600", upStationId, downStationId, 15);

        // when
        ExtractableResponse<Response> 지하철_노선_수정 = 지하철_노선_수정(지하철_노선_생성.jsonPath().getLong("id"), "신분당선", "bg-yellow-600");

        // then
        assertThat(지하철_노선_수정.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> 지하철_노선_조회 = 지하철_노선_조회(지하철_노선_생성.jsonPath().getLong("id"));
        수정된_지하철_노선_정보를_검증한다(지하철_노선_조회, "신분당선", "bg-yellow-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 생성하고 삭제하면 지하철 노선 조회 시 목록에서 제외된다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성 = 지하철_노선_생성("2호선", "bg-red-600", upStationId, downStationId, 15);

        // when
        ExtractableResponse<Response> 지하철_노선_삭제 = 지하철_노선_삭제(지하철_노선_생성.jsonPath().getLong("id"));

        // then
        assertThat(지하철_노선_삭제.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        지하철_노선_목록에서_삭제되었는지_검증한다(지하철_노선_이름_전체_목록());
    }
}
