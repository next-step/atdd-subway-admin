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

    @DisplayName("지하철노선 생성")
    @Test
    void create_line() {

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
