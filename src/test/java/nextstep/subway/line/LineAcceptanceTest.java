package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static nextstep.subway.line.LineTestUtils.*;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 생성_응답 = 지하철노선_생성_요청("2호선", "green", "강남역", "역삼역");

        // then
        지하철노선_생성_성공_확인(생성_응답);

        // then
        지하철노선_포함_확인("2호선");
    }

    /**
     * Given 지하철노선을 생성하고
     * When 기존에 존재하는 지하철노선 이름으로 지하철노선을 생성하면
     * Then 지하철노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철노선_생성_요청("2호선", "green", "강남역", "역삼역");

        // when
        ExtractableResponse<Response> 생성_응답 = 지하철노선_생성_요청("2호선", "green", "잠실역", "건대입구역");

        // then
        지하철노선_생성_실패_확인(생성_응답);
    }
}
