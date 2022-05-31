package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static nextstep.subway.line.LineAcceptanceFactory.지하철노선_생성;
import static nextstep.subway.section.SectionAcceptanceFactory.지하철구간_생성;
import static nextstep.subway.station.StationAcceptanceFactory.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    private StationResponse 신창역;
    private StationResponse 서울역;
    private StationResponse 소요산역;
    private LineResponse 일호선;

    @Autowired
    private LineRepository lineRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        소요산역 = 지하철역_생성("소요산역").as(StationResponse.class);
        신창역 = 지하철역_생성("신창역").as(StationResponse.class);
        서울역 = 지하철역_생성("서울역").as(StationResponse.class);
        일호선 = 지하철노선_생성("1호선", 소요산역.getId(), 신창역.getId(), "파란색", 20).as(LineResponse.class);
    }

    /**
     * When 지하철노선에 구간에 새로운 구간을 추가하면
     * Then 노선에 구간을 등록한다.
     */
    @Test
    void 역_사이에_새로운_역을_등록한다() {
        ExtractableResponse<Response> 일호선_구간 = 지하철구간_생성(일호선.getId(), 소요산역.getId(), 서울역.getId(), 10);

        assertThat(일호선_구간.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
