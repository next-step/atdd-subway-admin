package nextstep.subway.line;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("지하철 노선 관련 테스트")
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
     * - When 지하철 노선을 생성하면
     * - Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void create_line() {
        // given
        String 지하철노선 = "분당선";

    }

    @DisplayName("지하철노선 목록 조회")
    @Test
    void get_line_list() {

    }

    @DisplayName("지하철노선 조회")
    @Test
    void get_line() {

    }

    @DisplayName("지하철노선 수정")
    @Test
    void update_line() {

    }

    @DisplayName("지하철노선 삭제")
    @Test
    void delete_line() {

    }

}
