package nextstep.subway.line;

import static io.restassured.RestAssured.given;
import static nextstep.subway.utils.LineAcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.StationAcceptanceTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends AcceptanceTest {
    private LineRequest 일호선_생성;
    private LineRequest 구호선_생성;

    @BeforeEach
    public void setUp() {
        super.setUp();

        StationResponse 저장된_노량진역 = StationAcceptanceTestUtil.지하철역_생성("노량진역").as(StationResponse.class);
        StationResponse 저장된_용산역 = StationAcceptanceTestUtil.지하철역_생성("용산역").as(StationResponse.class);
        StationResponse 저장된_고속터미널역 = StationAcceptanceTestUtil.지하철역_생성("고속터미널역").as(StationResponse.class);
        StationResponse 저장된_신논현역 = StationAcceptanceTestUtil.지하철역_생성("신논현역").as(StationResponse.class);

        일호선_생성 = new LineRequest("일호선", "bg-blue-600", 저장된_노량진역.getId(), 저장된_용산역.getId(), 10);
        구호선_생성 = new LineRequest("구호선", "bg-yellow-600", 저장된_고속터미널역.getId(), 저장된_신논현역.getId(), 30);

    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        지하철노선_생성(일호선_생성);

        // then
        List<String> 조회된_지하철노선_목록 = 지하철노선_목록_조회();
        지하철노선_목록_검증_입력된_지하철노선이_존재(조회된_지하철노선_목록, "일호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철노선_생성(일호선_생성);
        지하철노선_생성(구호선_생성);

        // when
        List<String> 조회된_지하철노선_목록 = 지하철노선_목록_조회();

        // then
        assertThat(조회된_지하철노선_목록).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> 저장된_일호선 = 지하철노선_생성(일호선_생성);

        // when
        ExtractableResponse<Response> 조회된_지하철노선 = 지하철노선을_조회(저장된_일호선);

        // then
        지하철노선_검증_입력된_지하철노선이_존재(조회된_지하철노선, "일호선");
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 이름과 색상을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 저장된_일호선 = 지하철노선_생성(일호선_생성);

        // when
        지하철노선을_수정한다(저장된_일호선, "삼호선", "bg-yellow-500");

        // then
        ExtractableResponse<Response> 조회된_지하철노선 = 지하철노선을_조회(저장된_일호선);
        지하철노선_검증_입력된_지하철노선이_존재(조회된_지하철노선, "삼호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철노선을 제거한다.")
    void deleteLine() {
        // given
        ExtractableResponse<Response> 저장된_일호선 = 지하철노선_생성(일호선_생성);

        // when
        지하철노선을_삭제한다(저장된_일호선);

        // then
        List<String> 조회된_지하철노선_목록 = 지하철노선_목록_조회();
        지하철노선_목록_검증_입력된_지하철노선이_존재하지_않음(조회된_지하철노선_목록, "일호선");
    }

}
