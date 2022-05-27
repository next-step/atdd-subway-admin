package nextstep.subway.line;

import static nextstep.subway.AcceptanceTestFactory.목록_조회_성공_확인;
import static nextstep.subway.AcceptanceTestFactory.생성_성공_확인;
import static nextstep.subway.AcceptanceTestFactory.지하철_노선_목록_조회;
import static nextstep.subway.AcceptanceTestFactory.지하철_노선_생성;
import static nextstep.subway.AcceptanceTestFactory.지하철_역_생성;
import static nextstep.subway.AcceptanceTestFactory.지하철_역_생성_ID_추출;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When: 사용자는 지하철 노선 생성을 요청한다.
     * Then: 지하철 역이 생성된다.
     * Then: 사용자는 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        ExtractableResponse<Response> 지하철_노선_생성_응답_결과 =
                지하철_노선_생성("5호선", "공덕역", "애오개역");
        생성_성공_확인(지하철_노선_생성_응답_결과);
    }

    /**
     * Given: 2개의 지하철 노선이 생성되어 있다.
     * When: 사용자는 지하철 노선 목록 조회를 요청한다.
     * Then: 사용자는 2개의 지하철 노선 목록을 응답받는다.
     */
    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void getLines() {
        지하철_노선_생성("5호선", "공덕역", "애오개역");
        지하철_노선_생성("신분당선", "강남역", "양재역");

        List<String> 지하철_노선_목록 = 지하철_노선_목록_조회();
        목록_조회_성공_확인(지하철_노선_목록, "5호선", "신분당선");
    }
}
