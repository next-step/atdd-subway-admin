package nextstep.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;
import nextstep.subway.util.PreDataUtil;

@DisplayName("지하철구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    PreDataUtil preDataUtil;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        preDataUtil.truncate();
    }

    /**
     * When 노선에 지하철 역을 등록하면
     * Then 노선 조회 시 생성한 역이 포함된 것을 확인할 수 있다
     */
    @DisplayName("노선에 구간을 등록한다")
    @Test
    void createSection() {
    }
}
