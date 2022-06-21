package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.station.StationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    Long upStationId;
    Long downStationId;

    @BeforeEach
    public void setUp2() {
        upStationId = StationTestHelper.지하철역_생성하고_역_ID_응답("상행역");
        downStationId = StationTestHelper.지하철역_생성하고_역_ID_응답("하행역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        LineRequest lineRequest = new LineRequest("분당선", "red", upStationId, downStationId, 10);
        LineTestHelper.지하철_노선_생성(lineRequest);

        // then
        List<String> lineNames = 모든_지하철_노선_이름만_조회();
        assertThat(lineNames).contains("분당선");
    }

    private List<String> 모든_지하철_노선_이름만_조회() {
        return LineTestHelper.지하철_노선_전체목록_조회()
                .jsonPath().getList("name", String.class);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", "red", upStationId, downStationId, 10);
        LineTestHelper.지하철_노선_생성(lineRequest);

        lineRequest = new LineRequest("신분당선", "orange", upStationId, downStationId, 20);
        LineTestHelper.지하철_노선_생성(lineRequest);

        // when
        List<String> lineNames = 모든_지하철_노선_이름만_조회();

        // then
        assertThat(lineNames).contains("분당선", "신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회한다.")
    @Test
    void getLine() {
        // give
        LineRequest lineRequest = new LineRequest("분당선", "red", upStationId, downStationId, 10);
        Long lineId = LineTestHelper.지하철_노선_생성_하고_ID_응답(lineRequest);

        // when
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_ID로_조회(lineId);

        // then
        assertAll(
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(lineId),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("분당선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("red"),
                () -> assertThat(response.jsonPath().getList("stations")).hasSize(2)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정한다.")
    @Test
    void updateLine() {
        // give
        LineRequest lineRequest = new LineRequest("분당선", "red", upStationId, downStationId, 10);
        Long lineId = LineTestHelper.지하철_노선_생성_하고_ID_응답(lineRequest);

        // when
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest("다른분당선", "super-red");
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_수정(lineId, lineUpdateRequest);

        // then
        assertAll(
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(lineId),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("다른분당선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("super-red"),
                () -> assertThat(response.jsonPath().getList("stations")).hasSize(2)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제한다.")
    @Test
    void deleteLine() {
        //given
        LineRequest lineRequest = new LineRequest("분당선", "red", upStationId, downStationId, 10);
        Long lineId = LineTestHelper.지하철_노선_생성_하고_ID_응답(lineRequest);

        // then
        LineTestHelper.지하철_노선_ID로_삭제(lineId);

        // then
        List<String> lineNames = 모든_지하철_노선_이름만_조회();
        assertThat(lineNames).doesNotContain("분당선");
    }
}
