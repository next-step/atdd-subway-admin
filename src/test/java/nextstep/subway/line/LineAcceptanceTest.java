package nextstep.subway.line;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("노선역 기능(인수테스트)")
@Sql("/truncate.sql")
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
        When 지하철 노선을 생성하면
        Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("노선역을 생성한다.")
    void createLine() {

    }

    /**
         Given 2개의 지하철 노선을 생성하고
         When 지하철 노선 목록을 조회하면
         Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록 조회")
    void searchLines() {

    }

    /**
     Given 지하철 노선을 생성하고
     When 생성한 지하철 노선을 조회하면
     Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선 조회")
    void searchLine() {

    }

    /**
     Given 지하철 노선을 생성하고
     When 생성한 지하철 노선을 수정하면
     Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철노선 수정")
    void fixLine() {

    }

    /**
     Given 지하철 노선을 생성하고
     When 생성한 지하철 노선을 삭제하면
     Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철노선 삭제")
    void deleteLine() {

    }



}
