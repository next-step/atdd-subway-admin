package nextstep.subway.station;

import static nextstep.subway.utils.AssertionsUtils.assertOk;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_생성_요청;
import static nextstep.subway.utils.LineAcceptanceTestUtils.지하철_노선_조회_요청;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getIdAsLong;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.지하철_구간_생성_요청;
import static nextstep.subway.utils.StationsAcceptanceUtils.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import javax.annotation.Resource;
import nextstep.subway.config.AcceptanceTest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.section.CreateSectionRequest;
import nextstep.subway.utils.TearDownUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("구간 관련 기능")
@AcceptanceTest
public class SectionAcceptanceTest {

    private Long 신분당선;
    private Long 신논현역;
    private Long 삼성중앙역;

    @LocalServerPort
    int port;

    @Resource
    protected TearDownUtils tearDownUtils;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        tearDownUtils.tableClear();
        지하철_노선_생성();
    }

    private void 지하철_노선_생성() {
        LineResponse lineResponse = 지하철_노선_생성_요청("신분당선", "신논현역", "삼성중앙역").as(LineResponse.class);
        신논현역 = lineResponse.getStations().get(0).getId();
        삼성중앙역 = lineResponse.getStations().get(1).getId();
        신분당선 = lineResponse.getId();
    }

    /**
     * Given 노선을 생성한다.
     * When 노선에 구간을 추가한다.
     * Then 노선 조회 시, 추가한 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선에 구간을 추가한다.")
    public void addLineSection() {
        // Given
        Long 선정릉역 = getIdAsLong(지하철역_생성_요청("선정릉역"));
        CreateSectionRequest createSectionRequest = new CreateSectionRequest(신논현역, 선정릉역, 10);

        // When
        지하철_구간_생성_요청(신분당선, createSectionRequest);
        Response response = 지하철_노선_조회_요청(신분당선);

        // Then
        JsonPath jsonPath = response.jsonPath();
        assertAll(
            () -> assertOk(response),
            () -> assertThat(jsonPath.getList("stations.id", Long.class)).containsExactly(신논현역, 선정릉역, 삼성중앙역)
        );
    }
}
