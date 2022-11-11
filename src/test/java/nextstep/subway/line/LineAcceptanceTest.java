package nextstep.subway.line;

import static nextstep.subway.utils.LineAcceptanceTestUtils.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.StationAcceptanceTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse 저장된_강남역;
    private StationResponse 저장된_신논현역;
    private StationResponse 저장된_대성리역;
    private StationResponse 저장된_가평역;
    private LineRequest 신분당선_생성_요청;
    private LineRequest 경춘선_생성_요청;

    @BeforeEach
    public void setUp() {
        super.setUp();

        저장된_강남역 = StationAcceptanceTestUtils.지하철역을_생성한다(강남역).as(StationResponse.class);
        저장된_신논현역 = StationAcceptanceTestUtils.지하철역을_생성한다(신논현역).as(StationResponse.class);
        저장된_대성리역 = StationAcceptanceTestUtils.지하철역을_생성한다(대성리역).as(StationResponse.class);
        저장된_가평역 = StationAcceptanceTestUtils.지하철역을_생성한다(가평역).as(StationResponse.class);

        신분당선_생성_요청 = new LineRequest(신분당선, "bg-red-600", 저장된_강남역.getId(), 저장된_신논현역.getId(), 10);
        경춘선_생성_요청 = new LineRequest(경춘선, "bg-emerald-600", 저장된_대성리역.getId(), 저장된_가평역.getId(), 30);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // when
        지하철노선을_생성한다(신분당선_생성_요청);

        // then
        List<String> 조회된_지하철노선_목록 = 지하철노선_목록을_조회한다();
        지하철노선_목록_검증_입력된_지하철노선이_존재(조회된_지하철노선_목록, 신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록을 조회한다.")
    void getLines() {
        // given
        지하철노선을_생성한다(신분당선_생성_요청);
        지하철노선을_생성한다(경춘선_생성_요청);

        // when
        List<String> 조회된_지하철노선_목록 = 지하철노선_목록을_조회한다();

        // then
        지하철노선_목록_검증_입력된_지하철노선이_존재(조회된_지하철노선_목록, 신분당선, 경춘선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선을 조회한다.")
    void getLine() {
        // given
        ExtractableResponse<Response> 저장된_신분당선 = 지하철노선을_생성한다(신분당선_생성_요청);

        // when
        ExtractableResponse<Response> 조회된_지하철노선 = 지하철노선을_조회한다(저장된_신분당선);

        // then
        지하철노선_검증_입력된_지하철노선이_존재(조회된_지하철노선, 신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
}
