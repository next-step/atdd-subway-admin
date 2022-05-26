package nextstep.subway.line;

import static nextstep.subway.AcceptanceTestFactory.생성_성공_확인;
import static nextstep.subway.AcceptanceTestFactory.지하철_노선_생성;
import static nextstep.subway.AcceptanceTestFactory.지하철_역_생성;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

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
        지하철_역_생성("공덕역");
        지하철_역_생성("애오개역");
        ExtractableResponse<Response> 지하철_노선_생성_응답_결과 = 지하철_노선_생성("5호선");
        생성_성공_확인(지하철_노선_생성_응답_결과);
    }
}
