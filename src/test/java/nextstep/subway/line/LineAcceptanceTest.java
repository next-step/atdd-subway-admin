package nextstep.subway.line;

import static nextstep.subway.helper.JsonPathExtractor.getId;
import static nextstep.subway.line.LineAcceptanceTestFixture.createLine;
import static nextstep.subway.line.LineAcceptanceTestFixture.findAllLines;
import static nextstep.subway.line.LineAcceptanceTestFixture.findLine;
import static nextstep.subway.line.LineAcceptanceTestFixture.updateLine;
import static nextstep.subway.station.StationAcceptanceTestFixture.createStation;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.base.AcceptanceTest;
import nextstep.subway.helper.JsonPathExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends AcceptanceTest {
    private Long 판교역;
    private Long 청계산역;
    private Long 강남역;
    private Long 교대역;

    @BeforeEach
    void lineSetUp() {
        판교역 = getId(createStation("판교역"));
        청계산역 = getId(createStation("청계산역"));
        강남역 = getId(createStation("강남역"));
        교대역 = getId(createStation("교대역"));
    }

    /**
     *  When 지하철 노선을 생성하면
     *  Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // when
        createLine("신분당선", "bg-red-600", 10, 판교역, 청계산역);

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
        createLine("신분당선", "bg-red-600", 10, 판교역, 청계산역);
        createLine("2호선", "bg-green-600", 20, 강남역, 교대역);

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
        // given
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600", 10, 판교역, 청계산역);

        // when
        Long lineId = JsonPathExtractor.getId(createResponse);
        ExtractableResponse<Response> findLineResponse = findLine(lineId);

        // then
        assertThat(JsonPathExtractor.getId(findLineResponse)).isEqualTo(1);
        assertThat(JsonPathExtractor.getName(findLineResponse)).isEqualTo("신분당선");
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 생성한 지하철 노선을 수정하면
     *  Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLineTest() {
        // given
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600", 10, 판교역, 청계산역);

        // when
        Long lineId = JsonPathExtractor.getId(createResponse);
        updateLine(lineId, "다른분당선", "bg-red-700");

        // then
        ExtractableResponse<Response> findLineResponse = findLine(lineId);
        assertThat(JsonPathExtractor.getName(findLineResponse)).isEqualTo("다른분당선");
        assertThat(findLineResponse.jsonPath().getString("color")).isEqualTo("bg-red-700");
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 생성한 지하철 노선을 삭제하면
     *  Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLineTest() {
        // given
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600", 10, 판교역, 청계산역);

        // when
        Long lineId = JsonPathExtractor.getId(createResponse);
        LineAcceptanceTestFixture.deleteLine(lineId);

        // then
        ExtractableResponse<Response> findAllResponse = findAllLines();
        assertThat(JsonPathExtractor.getTotalJsonArraySize(findAllResponse)).isEqualTo(0);
    }
}
