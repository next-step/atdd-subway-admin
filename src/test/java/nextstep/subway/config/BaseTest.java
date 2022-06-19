package nextstep.subway.config;

import io.restassured.RestAssured;
import javax.annotation.Resource;
import nextstep.subway.utils.TearDownUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.server.LocalServerPort;

@AcceptanceTest
public class BaseTest {

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
    }
}
