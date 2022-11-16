package nextstep.subway.station;

import io.restassured.RestAssured;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    Map<String, Object> _1호선 = new HashMap<String, Object>(){
        {
            put("id", 1L);
            put("name", "1호선");
            put("color", "yellow darken-1");
        }
    };

    @BeforeEach
    public synchronized void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        long lineId = 지하철_노선을_생성한다(_1호선);

        // then
        지하철_노선_목록에서_조회된다(lineId);
    }

    private long 지하철_노선을_생성한다(Map<String, Object> line) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(line)
            .when().post("/lines")
            .then().log().all()
            .extract()
            .jsonPath().getLong("id");
    }

    private void 지하철_노선_목록에서_조회된다(long lineId) {
        RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .statusCode(HttpStatus.OK.value());
    }
}
